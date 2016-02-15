package com.jakubstas.foosie.service;

import com.jakubstas.foosie.configuration.FoosieProperties;
import com.jakubstas.foosie.service.model.Game;
import com.jakubstas.foosie.service.model.GamesCache;
import com.jakubstas.foosie.service.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class GameScheduler {

    private final Logger logger = LoggerFactory.getLogger(GameScheduler.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private GamesCache gamesCache;

    @Autowired
    private FoosieProperties foosieProperties;

    @Scheduled(fixedRate = 60_000)
    public void kickOffUpcomingGames() {
        logger.info("Game scheduler triggered!");

        for (final User host : gamesCache.getSetOfHosts()) {
            final Game game = gamesCache.findByHostName(host.getUserName());

            logger.info("Checking {}s game scheduled at {}", host.getUserName(), game.getScheduledTime());

            //if (shouldBeKickedOff(game)) {
                gameService.kickOffGame(game);
            //}
        }
    }

    private boolean shouldBeKickedOff(final Game game) {
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
