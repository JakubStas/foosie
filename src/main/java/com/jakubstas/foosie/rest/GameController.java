package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.service.GameService;
import com.jakubstas.foosie.validation.TwentyFourHourFormat;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController("game")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private SlackProperties slackProperties;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void createGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text", required = false) @TwentyFourHourFormat String proposedTime, @RequestParam(value = "response_url") String responseUrl) {
        if (slackProperties.getNewCommandToken().equals(token)) {
            gameService.createGame(userName, responseUrl, getProposedTimeAsDate(proposedTime));
        } else {
            logger.warn("Cannot create a new game - invalid token!");
        }
    }

    @RequestMapping(path = "join", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void joinGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text", required = false) @NotBlank String hostName, @RequestParam(value = "response_url") String responseUrl) {
        if (slackProperties.getIaminCommandToken().equals(token)) {
            final Optional<String> hostNameOptional = StringUtils.hasText(hostName) ? Optional.of(hostName) : Optional.empty();

            gameService.joinGame(userName, hostNameOptional, responseUrl);
        } else {
            logger.warn("Cannot join a game - invalid token!");
        }
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
