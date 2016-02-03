package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.slack.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST)
    public String ping(@RequestBody Hook hook) {
        logger.info("Message arrived: " + hook);

        return "Ping complete!";
    }
}
