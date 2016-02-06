package com.jakubstas.foosie.service;

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
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Autowired
    private SlackService slackService;

    private Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public void createGame(final @NotBlank String userId, final @GameUrl String messageUrl, final @TodayButFuture Date proposedTime) {
        final Game game = activeGames.get(userId);

        if (game == null) {
            logger.info("Creating a new game for {}", userId);

            final Game newGame = new Game(userId, messageUrl, proposedTime);

            activeGames.put(userId, newGame);

            replyWithGameCreated(userId, newGame);
        } else {
            logger.info("Active game already exists for {}", userId);

            replyWithExistingGame(game);
        }
    }

    private void replyWithExistingGame(final Game game) {
        final String message = String.format("You have already proposed a game at %s.", sdf.format(game.getScheduledTime()));

        slackService.postMessage(message);
    }

    private void replyWithGameCreated(final String userId, final Game game) {
        final String message = String.format("%s wants to play a game at %s. Who's in?", userId, sdf.format(game.getScheduledTime()));

        slackService.postMessage(message);
    }
}
