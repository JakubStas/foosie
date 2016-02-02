package com.jakubstas.foosie.rest;

import com.jakubstas.foosie.configuration.Defaults;
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
        slackService.postMessage(message);

        return "Ping complete!";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createGame(@RequestParam(value = "team", defaultValue = Defaults.defaultTeam) String team, @RequestParam(value = "msg", defaultValue = "Hello world!") String message) {
        slackService.postMessage("Recieved team=" + team + "; msg=" + message);

        return "Game set up!";
    }
}
