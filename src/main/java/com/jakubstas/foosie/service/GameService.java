package com.jakubstas.foosie.service;

import com.jakubstas.foosie.slack.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private SlackService slackService;

    public void createGame(final String userName, final String proposedTime) {
        final String message = String.format("%s wants to play a game at %s. Who's in?", userName, proposedTime);

        logger.info("Creating new game for {}", userName);

        slackService.postMessage(message);
    }
}
