package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.slack.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("ping")
public class PingController {

    @Autowired
    private SlackService slackService;

    @RequestMapping(method = RequestMethod.GET)
    public String ping(@RequestParam(value = "msg", defaultValue = "Hello world!") String message) {
        slackService.postMessageToChannel(message);

        return "Ping complete!";
    }
}
