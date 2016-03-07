package com.jakubstas.foosie.service;

import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.service.message.MessageTemplates;
import com.jakubstas.foosie.service.model.Game;
import com.jakubstas.foosie.service.model.GamesCache;
import com.jakubstas.foosie.service.model.User;
import com.jakubstas.foosie.slack.SlackService;
import com.jakubstas.foosie.validation.TwentyFourHourFormat;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm");

    @Autowired
    private SlackService slackService;

    @Autowired
    private GamesCache gamesCache;

    public void createGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "User ID cannot be empty!") String userId, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl, final @TwentyFourHourFormat String proposedTimeInHours) {
        final Game game = gamesCache.findByHostName(userName);

        if (game == null) {
            final Date proposedTime = getProposedTimeAsDate(proposedTimeInHours);
            final User host = new User(userName, userId);

            final Game newGame = new Game(host, messageUrl, proposedTime);
            gamesCache.hostGame(host, newGame);

            logger.info("Created a new game for {} scheduled at {}", userName, proposedTime);

            final PrivateReply gameCreatedReply = new PrivateReply(MessageTemplates.createGameInvitePrivateMessageForHostBody(proposedTimeInHours));
            slackService.postPrivateReplyToMessage(messageUrl, gameCreatedReply);

            logger.info("The host {} was notified that their game invite has been registered.", userName);

            slackService.postMessageToChannel(MessageTemplates.createGameInviteChannelMessageBody(userName, proposedTimeInHours));

            final PrivateReply hostJoinedGameReply = new PrivateReply(MessageTemplates.createGameLobbyHasBeenCreatedPrivateMessageBody(userName, proposedTimeInHours));
            slackService.postPrivateReplyToMessage(newGame.getGameMessageUrl(), hostJoinedGameReply);

            logger.info("The channel was notified about {}s game invite.", userName);
        } else {
            logger.info("Active game already exists for {}", userName);

            final PrivateReply alreadyActiveHostReply = new PrivateReply(MessageTemplates.createGameInviteAlreadyPostedPrivateMessageBody());
            slackService.postPrivateReplyToMessage(messageUrl, alreadyActiveHostReply);
        }
    }

    public void joinGame(final String userName, final String userId, final Optional<String> hostNameOptional, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl) {
        final User player = new User(userName, userId);

        if (hostNameOptional.isPresent()) {
            // join the game by host name
            final String hostName = hostNameOptional.get();
            final Game game = gamesCache.findByHostName(hostName);

            logger.info("Trying to join a game by {}", hostName);

            joinGameByHostName(player, hostName, game, messageUrl);
        } else {
            if (gamesCache.getNumberOfActiveGames() == 0) {
                logger.info("No active games at the moment.");

                final PrivateReply privateConfirmation = new PrivateReply(MessageTemplates.createNoActiveGamesToJoinPrivateMessageBody());
                slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);
            } else if (gamesCache.getNumberOfActiveGames() == 1) {
                // join the only active game
                final User host = gamesCache.getSetOfHosts().iterator().next();
                final Game game = gamesCache.findByHostName(host.getUserName());

                logger.info("Joining the only active game by {}", host.getUserName());

                joinGameByHostName(player, host.getUserName(), game, messageUrl);
            } else {
                // provide the host name
                final String activeHosts = gamesCache.getSetOfHosts().stream().map(user -> user.getUserName()).collect(Collectors.toList()).toString();

                logger.info("Several active games at the moment. Presenting {} with following options: {}", player.getUserName(), activeHosts);

                final PrivateReply privateConfirmation = new PrivateReply(MessageTemplates.createMultipleActiveGamesToJoinPrivateMessageBody(activeHosts));
                slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);
            }
        }
    }

    private void joinGameByHostName(final User player, final String hostName, final Game game, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl) {
        if (game == null) {
            logger.info("No active game by {} found!", hostName);

            final PrivateReply privateConfirmation = new PrivateReply(MessageTemplates.createNoActiveGamesToJoinByHostPrivateMessageBody(hostName));
            slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);

            return;
        }

        if (game.getPlayerByName(hostName).isPresent()) {
            logger.info("{} has already joined {}s game.", hostName, hostName);

            final PrivateReply privateReply = new PrivateReply(MessageTemplates.createAlreadyJoinedThisGamePrivateMessageBody());
            slackService.postPrivateReplyToMessage(messageUrl, privateReply);
        } else {
            logger.info("Adding {} to {}s game.", hostName, hostName);

            game.join(player);

            final PrivateReply userJoinedGameReply = new PrivateReply(MessageTemplates.createGameLobbyPlayerStatusPrivateMessageBody(player.getUserName()));
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), userJoinedGameReply);

            logger.info("The host was notified that {} joined their game.", hostName);

            final String scheduledTime = dateTimeFormatter.print(game.getScheduledTime().getTime());
            final PrivateReply privateConfirmation = new PrivateReply(MessageTemplates.createSuccessfullyJoinedGamePrivateMessageBody(game.getHost().getUserName(), scheduledTime));
            slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);

            logger.info("The user was notified that they joined {}s game.", hostName);
        }
    }

    public void cancelGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "Response URL cannot be empty!") String responseUrl) {
        final Game game = gamesCache.findByHostName(userName);

        if (game != null) {
            logger.info("Cancelling the game by {}", userName);

            gamesCache.cancelGameByHost(userName);

            final PrivateReply hostCancelledGameReply = new PrivateReply(MessageTemplates.createSuccessfullyCancelledGamePrivateMessageBody(userName));
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), hostCancelledGameReply);

            logger.info("The host was notified that {} joined their game.", userName);

            slackService.postMessageToChannel(MessageTemplates.createGameCancelledChannelMessageBody(userName));

            logger.info("The channel was notified that {}s game has been cancelled.", userName);
        } else {
            logger.info("There are no active games by {} to cancel", userName);

            final PrivateReply cancelNotAvailableReply = new PrivateReply(MessageTemplates.createUnableToCancelGamePrivateMessageBody());
            slackService.postPrivateReplyToMessage(responseUrl, cancelNotAvailableReply);
        }
    }

    public void updateGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "Response URL cannot be empty!") String responseUrl, final @TwentyFourHourFormat String proposedTimeInHours) {
        final Game game = gamesCache.findByHostName(userName);

        if (game != null) {
            final Date proposedTime = getProposedTimeAsDate(proposedTimeInHours);

            logger.info("Updating {}s game time from {} to {}", userName, game.getScheduledTime(), proposedTimeInHours);

            game.reschedule(proposedTime);

            final PrivateReply hostRescheduledGameReply = new PrivateReply(MessageTemplates.createSuccessfullyRescheduledGamePrivateMessageBody(proposedTimeInHours));
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), hostRescheduledGameReply);

            logger.info("The host was notified that their game was rescheduled.");

            slackService.postMessageToChannel(MessageTemplates.createGameRescheduledChannelMessageBody(userName, proposedTimeInHours));

            logger.info("The channel was notified that {}s game has been rescheduled.", userName);
        } else {
            logger.info("There are no active games by {} to update", userName);

            final PrivateReply cancelNotAvailableReply = new PrivateReply(MessageTemplates.createUnableToRescheduleGamePrivateMessageBody());
            slackService.postPrivateReplyToMessage(responseUrl, cancelNotAvailableReply);
        }
    }

    public void kickOffGame(final Game game) throws UnsupportedEncodingException {
        logger.info("Kicking off game hosted by {}", game.getHost().getUserName());

        for (User player : game.getPlayers()) {
            logger.info("Pinging: " + player.getUserName());

            final String gameKickOffMessage = String.format("Message from Foosie: %ss game is about to start. Head off to the table and get ready for your game!", player.getUserName());
            slackService.postPrivateMessageToPlayer(player, gameKickOffMessage);
        }

        final String gameKickOffMessage = String.format("%ss game is about to start. Head off to the table and get ready for your game!", game.getHost().getUserName());
        slackService.postPrivateMessageToPlayer(game.getHost(), gameKickOffMessage);

        gamesCache.cancelGameByHost(game.getHost().getUserName());

        final String gameStartedMessage = String.format("%ss game has started", game.getHost().getUserName());
        slackService.postMessageToChannel(gameStartedMessage);

        logger.info("The game hosted by {} has just started and has been removed from active games list.", game.getHost().getUserName());
    }

    public void getStatus(final @NotBlank(message = "Response URL cannot be empty!") String responseUrl) {
        final String statusReplyMessage;
        if (gamesCache.getNumberOfActiveGames() == 0) {
            statusReplyMessage = "There are no active games right now.";
        } else {
            int i = 1;
            final StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("Active games:\n\n");

            for (final User host : gamesCache.getSetOfHosts()) {
                final Game game = gamesCache.findByHostName(host.getUserName());
                final String scheduledTime = dateTimeFormatter.print(game.getScheduledTime().getTime());
                final String gameStatus = String.format("%d. hosted by %s starts at %s (%d player(s))\n", i, host.getUserName(), scheduledTime, game.getPlayers().size() + 1);
                stringBuffer.append(gameStatus);
                i++;
            }

            statusReplyMessage = stringBuffer.toString();
        }

        final PrivateReply statusReply = new PrivateReply(statusReplyMessage);
        slackService.postPrivateReplyToMessage(responseUrl, statusReply);

        logger.info("The user was presented with current status.");
    }

    private Date getProposedTimeAsDate(final String proposedTime) {
        final String hours = proposedTime.split(":")[0];
        final String minutes = proposedTime.split(":")[1];

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
        cal.set(Calendar.MINUTE, Integer.parseInt(minutes));
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }
}
