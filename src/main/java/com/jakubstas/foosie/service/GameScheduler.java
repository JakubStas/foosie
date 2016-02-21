package com.jakubstas.foosie.service;

import com.jakubstas.foosie.configuration.FoosieProperties;
import com.jakubstas.foosie.service.model.Game;
import com.jakubstas.foosie.service.model.GamesCache;
import com.jakubstas.foosie.service.model.User;
import com.jakubstas.foosie.slack.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

@Component
public class GameScheduler {

    private final Logger logger = LoggerFactory.getLogger(GameScheduler.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private GamesCache gamesCache;

    @Autowired
    private SlackService slackService;

    @Autowired
    private FoosieProperties foosieProperties;

    private final String gameExpiredPrivateMessageTemplate = "Message from Foosie: %ss game has expired! Try hosting a new game later.";

    private final String gameExpiredChannelMessageTemplate = "%ss game has expired!";

    @Scheduled(fixedRate = 60_000)
    public void kickOffUpcomingGames() throws UnsupportedEncodingException {
        for (final User host : gamesCache.getSetOfHosts()) {
            final Game game = gamesCache.findByHostName(host.getUserName());

            logger.info("Checking {}s game scheduled at {}", host.getUserName(), game.getScheduledTime());

            if (isItTheTimeToKickOffTheGame(game)) {
                if (hasTheGameExpired(game)) {
                    logger.info("Expired game hosted by {} detected! Notifying players and host and removing from the active games set", game.getHost().getUserName());

                    final String gameExpiredPrivateMessage = String.format(gameExpiredPrivateMessageTemplate, game.getHost().getUserName());

                    for (User player : game.getPlayers()) {
                        slackService.postPrivateMessageToPlayer(player, gameExpiredPrivateMessage);
                    }

                    slackService.postPrivateMessageToPlayer(game.getHost(), gameExpiredPrivateMessage);

                    gamesCache.cancelGameByHost(game.getHost().getUserName());

                    final String gameExpiredChannelMessage = String.format(gameExpiredChannelMessageTemplate, game.getHost().getUserName());
                    slackService.postMessageToChannel(gameExpiredChannelMessage);

                    logger.info("The game hosted by {} has just expired and has been removed from active games list.", game.getHost().getUserName());
                }

                if (game.isReady()) {
                    gameService.kickOffGame(game);
                }
            }
        }
    }

    private boolean hasTheGameExpired(final Game game) {
        if (isItTheTimeToKickOffTheGame(game) && !game.isReady()) {
            return true;
        }

        return false;
    }

    private boolean isItTheTimeToKickOffTheGame(final Game game) {
        final Calendar calNow = Calendar.getInstance();
        final Calendar calGame = Calendar.getInstance();
        calGame.setTime(game.getScheduledTime());

        final boolean yearMatches = calNow.get(Calendar.YEAR) == calGame.get(Calendar.YEAR);
        final boolean monthMatches = calNow.get(Calendar.MONTH) == calGame.get(Calendar.MONTH);
        final boolean dayMatches = calNow.get(Calendar.DAY_OF_MONTH) == calGame.get(Calendar.DAY_OF_MONTH);
        final boolean hourMatches = calNow.get(Calendar.HOUR_OF_DAY) == calGame.get(Calendar.HOUR_OF_DAY);

        final int nowMinutes = calNow.get(Calendar.MINUTE);
        final int gameMinutes = calGame.get(Calendar.MINUTE);

        final boolean minutesMatch = nowMinutes + foosieProperties.getScheduleBefore() == gameMinutes;

        logger.info("Minute match: " + (minutesMatch ? "yes" : "no"));

        return yearMatches && monthMatches && dayMatches && hourMatches && minutesMatch;
    }
}
