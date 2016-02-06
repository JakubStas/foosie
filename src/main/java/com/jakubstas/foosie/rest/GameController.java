package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.service.GameService;
import com.jakubstas.foosie.validation.TwentyFourHourFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController("game")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private SlackProperties slackProperties;

    private final Pattern timePattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void createGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text", required = false) @TwentyFourHourFormat String proposedTime, @RequestParam(value = "response_url") String responseUrl) {
        logger.info("token: " + token);
        logger.info("userName: " + userName);
        logger.info("proposedTime: " + proposedTime);
        logger.info("responseUrl: " + responseUrl);

        final Matcher matcher = timePattern.matcher(proposedTime);

        if (matcher.matches()) {
            gameService.createGame(userName, responseUrl, getProposedTimeAsDate(proposedTime));

        } else {
            logger.warn("Invalid proposed time!");
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

    @RequestMapping(path = "join", method = RequestMethod.POST, value = "join", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void joinGame(@RequestParam(value = "token", required = false) String token,
                         @RequestParam(value = "user_name", required = false) String userName) {
        logger.info("token: " + token);
        logger.info("expected token: " + slackProperties.getIaminCommandToken());
        logger.info("userName: " + userName);

//        if (slackProperties.getIaminCommandToken().equals(token)) {
//        logger.info("User {} decided to join the game.", userName);
//
//        final GameResponse gameResponse = new GameResponse(":ballot_box_with_check: " + userName);
//
//        logger.info("Response is ready.");
//
//        slackService.quickReply(mostRecentResponseUrl, gameResponse);
//        } else {
//            logger.warn("Invalid Iamin Slack token!");
//        }
    }

}
