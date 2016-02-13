package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("game")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private SlackProperties slackProperties;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void createGame(@RequestParam(value = "response_url") String responseUrl, @RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "user_id") String userId, @RequestParam(value = "text") String proposedTime) {
        if (slackProperties.getNewCommandToken().equals(token)) {
            gameService.createGame(userName, userId, responseUrl, proposedTime);
        } else {
            logger.warn("Cannot create a new game - invalid token!");
        }
    }

    @RequestMapping(path = "join", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void joinGame(@RequestParam(value = "response_url") String responseUrl, @RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "user_id") String userId, @RequestParam(value = "text", required = false) String hostName) {
        if (slackProperties.getIaminCommandToken().equals(token)) {
            final Optional<String> hostNameOptional = StringUtils.hasText(hostName) ? Optional.of(hostName) : Optional.empty();

            gameService.joinGame(userName, userId, hostNameOptional, responseUrl);
        } else {
            logger.warn("Cannot join a game - invalid token!");
        }
    }

    @RequestMapping(path = "update", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void updateGame(@RequestParam(value = "response_url") String responseUrl, @RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text") String proposedTime) {
        if (slackProperties.getUpdateCommandToken().equals(token)) {
            gameService.updateGame(userName, responseUrl, proposedTime);
        } else {
            logger.warn("Cannot update a game - invalid token!");
        }
    }

    @RequestMapping(path = "cancel", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void cancelGame(@RequestParam(value = "response_url") String responseUrl, @RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName) {
        if (slackProperties.getCancelCommandToken().equals(token)) {
            gameService.cancelGame(userName, responseUrl);
        } else {
            logger.warn("Cannot cancel a game - invalid token!");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getStatus(@RequestParam(value = "response_url") String responseUrl, @RequestParam(value = "token") String token) {
        if (slackProperties.getStatusCommandToken().equals(token)) {
            gameService.getStatus(responseUrl);
        } else {
            logger.warn("Cannot show status - invalid token!");
        }
    }
}
