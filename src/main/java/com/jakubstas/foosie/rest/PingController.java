package com.jakubstas.foosie.rest;

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

    @Autowired
    private SlackService slackService;

    @RequestMapping(method = RequestMethod.GET)
    public String ping(@RequestParam(value = "msg", defaultValue = "Hello world!") String message) {
        slackService.postMessage(message);

        return "Ping complete!";
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String ping(@RequestParam(value = "token") String token,
                       @RequestParam(value = "team_id") String teamId,
                       @RequestParam(value = "team_domain") String teamDomain,
                       @RequestParam(value = "channel_id") String channelId,
                       @RequestParam(value = "channel_name") String channelName,
                       @RequestParam(value = "command") String command,
                       @RequestParam(value = "user_id") String userId,
                       @RequestParam(value = "user_name") String userame,
                       @RequestParam(value = "text") String text,
                       @RequestParam(value = "response_url") String responseUrl
    ) {
        logger.info("token: " + token);
        logger.info("teamId: " + teamId);
        logger.info("teamDomain: " + teamDomain);
        logger.info("channelId: " + channelId);
        logger.info("channelName: " + channelName);
        logger.info("command: " + command);
        logger.info("userId: " + userId);
        logger.info("userame: " + userame);
        logger.info("text: " + text);
        logger.info("responseUrl: " + responseUrl);

        return "Ping complete!";
    }
}
