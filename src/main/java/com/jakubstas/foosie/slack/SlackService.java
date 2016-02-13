package com.jakubstas.foosie.slack;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.service.model.User;
import com.jakubstas.foosie.slack.json.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class SlackService {

    private final Logger logger = LoggerFactory.getLogger(SlackService.class);

    private final String postPrivateMessagePath = "https://slack.com/api/chat.postMessage";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SlackProperties slackProperties;

    public void postMessageToChannel(final String message) {
        logger.info("Posting message: {}", message);

        final URI uri = URI.create(slackProperties.getIncomingWebHookUri());
        final SlackMessage slackMessage = new SlackMessage(message);

        restTemplate.postForLocation(uri, slackMessage);
    }

    public void postPrivateReplyToMessage(final String uriAsString, final PrivateReply privateReply) {
        logger.info("Posting quick reply to: {}", uriAsString);

        final URI uri = URI.create(uriAsString);

        logger.info("URI = " + uri.toString());
        logger.info("Message = " + privateReply.getText());

        restTemplate.postForLocation(uri, privateReply);
    }

    public void postPrivateMessageToPlayer(final User player, final String text) {
        logger.info("Posting private message to user: {}", player.getUserName());

        final String uriAsString = UriComponentsBuilder.fromPath(postPrivateMessagePath).queryParam("token", slackProperties.getAuthToken()).queryParam("channel", player.getUserId()).queryParam("text", text).queryParam("pretty", 1).build(true).toString();

        final URI uri = URI.create(uriAsString);

        logger.info("URI = " + uri.toString());
        logger.info("Message = " + text);

        restTemplate.postForLocation(uri, null);
    }
}
