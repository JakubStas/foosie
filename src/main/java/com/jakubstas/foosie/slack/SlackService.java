package com.jakubstas.foosie.slack;

import com.jakubstas.foosie.slack.model.SlackMessage;
import com.jakubstas.foosie.configuration.SlackProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class SlackService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SlackProperties slackProperties;

    public void postMessage(final String message) {
        final URI uri = URI.create(slackProperties.getIncomingWebhook());
        final SlackMessage slackMessage = new SlackMessage(message);

        restTemplate.postForLocation(uri, slackMessage);
    }

}
