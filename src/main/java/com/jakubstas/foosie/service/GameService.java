package com.jakubstas.foosie.service;

import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.slack.SlackService;
import com.jakubstas.foosie.validation.GameUrl;
import com.jakubstas.foosie.validation.TodayButFuture;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Autowired
    private SlackService slackService;

    private Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public void createGame(final @NotBlank String userName, final @GameUrl String messageUrl, final @TodayButFuture Date proposedTime) {
        final Game game = activeGames.get(userName);

        if (game == null) {
            final Game newGame = new Game(userName, messageUrl, proposedTime);
            activeGames.put(userName, newGame);

            logger.info("Created a new game for {} scheduled at {}", userName, proposedTime);

            final String gameCreatedMessage = String.format("You game invite has been posted. The game is scheduled for %s and following players joined in:", sdf.format(proposedTime));
            final PrivateReply gameCreatedReply = new PrivateReply(gameCreatedMessage);
            slackService.postPrivateReplyToMessage(messageUrl, gameCreatedReply);

            logger.info("The host {} notified that their game invite has been registered.", userName);

            final String channelInviteMessage = String.format("%s wants to play a game at %s. Who's in?", userName, sdf.format(proposedTime));
            slackService.postMessageToChannel(channelInviteMessage);

            final String hostJoinedGameReplyMessage = String.format("%ss game @%s\n:ballot_box_with_check: ", userName, sdf.format(proposedTime));
            final PrivateReply hostJoinedGameReply = new PrivateReply(hostJoinedGameReplyMessage);
            slackService.postPrivateReplyToMessage(newGame.getGameMessageUrl(), hostJoinedGameReply);

            logger.info("The channel notified about {}s game invite.", userName);
        } else {
            logger.info("Active game already exists for {}", userName);

            final PrivateReply alreadyActiveHostReply = new PrivateReply("Active game created by you already exists! There is nothing left to do but wait :smile:");
            slackService.postPrivateReplyToMessage(messageUrl, alreadyActiveHostReply);
        }
    }

    public void joinGame(final @NotBlank String userName, final Optional<String> hostNameOptional, final @GameUrl String messageUrl) {
        if (hostNameOptional.isPresent()) {
            // join the game by host name
            final String hostName = hostNameOptional.get();
            final Game game = activeGames.get(hostName);

            logger.info("Trying to join a game by {}", hostName);

            joinGameByHostName(userName, hostName, game, messageUrl);
        } else {
            if (activeGames.size() == 1) {
                // join the only active game
                final String hostName = activeGames.keySet().iterator().next();
                final Game game = activeGames.get(0);

                logger.info("Joining the only active game by {}", hostName);

                joinGameByHostName(userName, hostName, game, messageUrl);
            } else {
                // provide the host name
                final String activeHosts = activeGames.keySet().toString();

                logger.info("Several active games at the moment. Presenting {} with following options: {}", userName, activeHosts);

                final PrivateReply privateConfirmation = new PrivateReply("There are several active games at the moment. Pick the one that you like - " + activeHosts);
                slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);
            }
        }
    }

    private void joinGameByHostName(final @NotBlank String userName, final String hostName, final Game game, final @GameUrl String messageUrl) {
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

            logger.info("The host notified that {} joined their game.", userName);

            final String privateConfirmationMessage = String.format("You have successfully joined game by %s starting at %s", game.getPlayerIds().get(0), sdf.format(game.getScheduledTime()));
            final PrivateReply privateConfirmation = new PrivateReply(privateConfirmationMessage);
            slackService.postPrivateReplyToMessage(messageUrl, privateConfirmation);

            logger.info("The user notified that they joined {}s game.", hostName);
        }
    }
}
