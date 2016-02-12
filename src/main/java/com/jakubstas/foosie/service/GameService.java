package com.jakubstas.foosie.service;

import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.service.model.Game;
import com.jakubstas.foosie.service.model.GamesCache;
import com.jakubstas.foosie.slack.SlackService;
import com.jakubstas.foosie.validation.TwentyFourHourFormat;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Validated
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Autowired
    private SlackService slackService;

    @Autowired
    private GamesCache gamesCache;

    public void createGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl, final @TwentyFourHourFormat String proposedTimeInHours) {
        final Game game = gamesCache.findByHostName(userName);

        if (game == null) {
            final Date proposedTime = getProposedTimeAsDate(proposedTimeInHours);

            final Game newGame = new Game(userName, messageUrl, proposedTime);
            gamesCache.addGame(userName, newGame);

            logger.info("Created a new game for {} scheduled at {}", userName, proposedTime);

            final String gameCreatedMessage = String.format("Your game invite has been posted. The game is scheduled for %s and following players joined in:", sdf.format(proposedTime));
            final PrivateReply gameCreatedReply = new PrivateReply(gameCreatedMessage);
            slackService.postPrivateReplyToMessage(messageUrl, gameCreatedReply);

            logger.info("The host {} was notified that their game invite has been registered.", userName);

            final String channelInviteMessage = String.format("%s wants to play a game at %s. Who's in?", userName, sdf.format(proposedTime));
            slackService.postMessageToChannel(channelInviteMessage);

            final String hostJoinedGameReplyMessage = String.format("Lobby for %ss game starting at %s\n:ballot_box_with_check: %s", userName, sdf.format(proposedTime), userName);
            final PrivateReply hostJoinedGameReply = new PrivateReply(hostJoinedGameReplyMessage);
            slackService.postPrivateReplyToMessage(newGame.getGameMessageUrl(), hostJoinedGameReply);

            logger.info("The channel was notified about {}s game invite.", userName);
        } else {
            logger.info("Active game already exists for {}", userName);

            final PrivateReply alreadyActiveHostReply = new PrivateReply("Active game created by you already exists! There is nothing left to do but wait :smile:");
            slackService.postPrivateReplyToMessage(messageUrl, alreadyActiveHostReply);
        }
    }

    public void joinGame(final @NotBlank(message = "Username cannot be empty!") String userName, final Optional<String> hostNameOptional, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl) {
        if (hostNameOptional.isPresent()) {
            // join the game by host name
            final String hostName = hostNameOptional.get();
            final Game game = gamesCache.findByHostName(hostName);

            logger.info("Trying to join a game by {}", hostName);

            joinGameByHostName(userName, hostName, game, messageUrl);
        } else {
            if (gamesCache.getNumberOfActiveGames() == 1) {
                // join the only active game
                final String hostName = gamesCache.getSetOfHostNames().iterator().next();
                final Game game = gamesCache.findByHostName(hostName);

                logger.info("Joining the only active game by {}", hostName);

                joinGameByHostName(userName, hostName, game, messageUrl);
            } else {
                // provide the host name
                final String activeHosts = gamesCache.getSetOfHostNames().toString();

                logger.info("Several active games at the moment. Presenting {} with following options: {}", userName, activeHosts);

                final PrivateReply privateConfirmation = new PrivateReply("There are several active games at the moment. Pick the one that you like - " + activeHosts);
                slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);
            }
        }
    }

    private void joinGameByHostName(final @NotBlank(message = "Username cannot be empty!") String userName, final String hostName, final Game game, final @NotBlank(message = "Response URL cannot be empty!") String messageUrl) {
        if (game == null) {
            logger.info("No active game by {} found!", hostName);

            final String noActiveGameMessage = String.format("There is no active game by %s at the moment. Try creating a new one yourself!", hostName);
            final PrivateReply privateConfirmation = new PrivateReply(noActiveGameMessage);
            slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);

            return;
        }

        if (game.getPlayerIds().contains(userName)) {
            logger.info("{} has already joined {}s game.", userName, hostName);

            final PrivateReply privateReply = new PrivateReply("You have already joined this game! Nothing left but wait for other players to join in");
            slackService.postPrivateReplyToMessage(messageUrl, privateReply);
        } else {
            logger.info("Adding {} to {}s game.", userName, hostName);

            game.getPlayerIds().add(userName);

            final PrivateReply userJoinedGameReply = new PrivateReply(":ballot_box_with_check: " + userName);
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), userJoinedGameReply);

            logger.info("The host was notified that {} joined their game.", userName);

            final String privateConfirmationMessage = String.format("You have successfully joined game by %s starting at %s", game.getPlayerIds().get(0), sdf.format(game.getScheduledTime()));
            final PrivateReply privateConfirmation = new PrivateReply(privateConfirmationMessage);
            slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);

            logger.info("The user was notified that they joined {}s game.", hostName);
        }
    }

    public void cancelGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "Response URL cannot be empty!") String responseUrl) {
        final Game game = gamesCache.findByHostName(userName);

        if (game != null) {
            logger.info("Cancelling the game by {}", userName);

            gamesCache.cancelGameByHost(userName);

            final PrivateReply hostCancelledGameReply = new PrivateReply("Your game has been successfully cancelled!");
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), hostCancelledGameReply);

            logger.info("The host was notified that {} joined their game.", userName);

            final String hostCancelledGameReplyMessage = String.format("Lobby for %ss game has been closed. The game is cancelled!", userName);
            slackService.postMessageToChannel(hostCancelledGameReplyMessage);

            logger.info("The channel was notified that {}s game has been cancelled.", userName);
        } else {
            logger.info("There are no active games by {} to cancel", userName);

            final PrivateReply cancelNotAvailableReply = new PrivateReply("You have no active games to be cancelled.");
            slackService.postPrivateReplyToMessage(responseUrl, cancelNotAvailableReply);
        }
    }

    public void updateGame(final @NotBlank(message = "Username cannot be empty!") String userName, final @NotBlank(message = "Response URL cannot be empty!") String responseUrl, final @TwentyFourHourFormat String proposedTimeInHours) {
        final Game game = gamesCache.findByHostName(userName);

        if (game != null) {
            final Date proposedTime = getProposedTimeAsDate(proposedTimeInHours);

            logger.info("Updating {}s game time from {} to {}", userName, game.getScheduledTime(), sdf.format(proposedTime));

            game.reschedule(proposedTime);

            final String hostRescheduledGameMessage = String.format("Your game has been successfully rescheduled to %s!", sdf.format(proposedTime));
            final PrivateReply hostRescheduledGameReply = new PrivateReply(hostRescheduledGameMessage);
            slackService.postPrivateReplyToMessage(game.getGameMessageUrl(), hostRescheduledGameReply);

            logger.info("The host was notified that their game was rescheduled.");

            final String hostRescheduledGameReplyMessage = String.format("%ss game has been rescheduled to %s", userName, sdf.format(proposedTime));
            slackService.postMessageToChannel(hostRescheduledGameReplyMessage);

            logger.info("The channel was notified that {}s game has been rescheduled.", userName);
        } else {
            logger.info("There are no active games by {} to update", userName);

            final PrivateReply cancelNotAvaliableReply = new PrivateReply("You have no active games to be rescheduled.");
            slackService.postPrivateReplyToMessage(responseUrl, cancelNotAvaliableReply);
        }
    }

    public void getStatus(final @NotBlank(message = "Response URL cannot be empty!") String responseUrl) {
        final String statusReplyMessage;
        if (gamesCache.getNumberOfActiveGames() == 0) {
            statusReplyMessage = "There are no active games right now.";
        } else {
            int i = 1;
            final StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("Active games:\n\n");

            for (final String hostName : gamesCache.getSetOfHostNames()) {
                final Game game = gamesCache.findByHostName(hostName);
                final String gameStatus = String.format("%d. hosted by %s starts at %s (%d player(s))\n", i, hostName, sdf.format(game.getScheduledTime()), game.getPlayerIds().size());
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
