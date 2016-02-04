package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.slack.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("ping")
public class PingController {

    private final Logger logger = LoggerFactory.getLogger(PingController.class);

    private String mostRecentResponseUrl;

    @Autowired
    private SlackService slackService;


    @Autowired
    private SlackProperties slackProperties;

    @RequestMapping(method = RequestMethod.GET)
    public String ping(@RequestParam(value = "msg", defaultValue = "Hello world!") String message) {
        slackService.postMessage(message);

        return "Ping complete!";
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public void joinGame(@RequestParam(value = "token", required = false) String token,
                         @RequestParam(value = "user_name", required = false) String userName) {
        logger.info("token: " + token);
        logger.info("expected token: " + slackProperties.getIaminCommandToken());
        logger.info("userName: " + userName);

//        if (slackProperties.getIaminCommandToken().equals(token)) {
        logger.info("User {} decided to join the game.", userName);

        final GameResponse gameResponse = new GameResponse(":ballot_box_with_check: " + userName);

        logger.info("Response is ready.");

        slackService.quickReply(mostRecentResponseUrl, gameResponse);
//        } else {
//            logger.warn("Invalid Iamin Slack token!");
//        }
    }

    public void setMostRecentResponseUrl(String mostRecentResponseUrl) {
        this.mostRecentResponseUrl = mostRecentResponseUrl;
    }
}
