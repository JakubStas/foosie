package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController("game")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private SlackProperties slackProperties;

    private final Pattern timePattern = Pattern.compile("[0-9]{2}:[0-9]{2}");

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void createGame(@RequestParam(value = "token", required = false) String token,
                           @RequestParam(value = "user_name", required = false) String userName,
                           @RequestParam(value = "text", required = false) String text,
                           @RequestParam(value = "response_url", required = false) String responseUrl) {
        logger.info("token: " + token);
        logger.info("userName: " + userName);
        logger.info("text: " + text);
        logger.info("responseUrl: " + responseUrl);

        if (slackProperties.getSlashCommandToken().equals(token)) {
            final Matcher matcher = timePattern.matcher(text);

            if (matcher.matches()) {
                gameService.createGame(userName, text);
            } else {
                logger.warn("Invalid proposed time!");
            }
        } else {
            logger.warn("Invalid Slack token!");
        }
    }
}
