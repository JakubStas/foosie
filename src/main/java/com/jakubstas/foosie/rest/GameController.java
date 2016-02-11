package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.service.GameService;
import com.jakubstas.foosie.slack.SlackService;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController("game")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private SlackService slackService;

    @Autowired
    private SlackProperties slackProperties;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void createGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text") @TwentyFourHourFormat String proposedTime, @RequestParam(value = "response_url") String responseUrl) {
        try {
            if (slackProperties.getNewCommandToken().equals(token)) {
                gameService.createGame(userName, responseUrl, getProposedTimeAsDate(proposedTime));
            } else {
                logger.warn("Cannot create a new game - invalid token!");
            }
        } catch (ConstraintViolationException e) {
            final StringBuffer stringBuffer = new StringBuffer();
            int i = 1;

            stringBuffer.append("Your command fail validation! Please review following issues:\n\n");

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                final String violationMessage = String.format("%d. %s\n", i, violation.getMessage());
                stringBuffer.append(violationMessage);
                i++;
            }

            final PrivateReply statusReply = new PrivateReply(stringBuffer.toString());
            slackService.postPrivateReplyToMessage(responseUrl, statusReply);

            logger.info("The list of validation errors was returned to the user.");
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

    @RequestMapping(path = "update", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void updateGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "text") @TwentyFourHourFormat String proposedTime, @RequestParam(value = "response_url") String responseUrl) {
        if (slackProperties.getUpdateCommandToken().equals(token)) {
            gameService.updateGame(userName, responseUrl, getProposedTimeAsDate(proposedTime));
        } else {
            logger.warn("Cannot update a game - invalid token!");
        }
    }

    @RequestMapping(path = "cancel", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void cancelGame(@RequestParam(value = "token") String token, @RequestParam(value = "user_name") String userName, @RequestParam(value = "response_url") String responseUrl) {
        if (slackProperties.getCancelCommandToken().equals(token)) {
            gameService.cancelGame(userName, responseUrl);
        } else {
            logger.warn("Cannot cancel a game - invalid token!");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getStatus(@RequestParam(value = "token") String token, @RequestParam(value = "response_url") String responseUrl) {
        if (slackProperties.getStatusCommandToken().equals(token)) {
            gameService.getStatus(responseUrl);
        } else {
            logger.warn("Cannot show status - invalid token!");
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
