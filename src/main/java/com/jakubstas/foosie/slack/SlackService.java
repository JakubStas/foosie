package com.jakubstas.foosie.slack;

import com.jakubstas.foosie.slack.model.SlackMessage;
import com.jakubstas.foosie.configuration.SlackProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class SlackService {

    private final Logger logger = LoggerFactory.getLogger(SlackService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SlackProperties slackProperties;

    public void postMessage(final String message) {
        logger.info("Posting message: {}", message);

        final URI uri = URI.create(slackProperties.getIncomingWebhook());
        final SlackMessage slackMessage = new SlackMessage(message);

        restTemplate.postForLocation(uri, slackMessage);
    }

}
