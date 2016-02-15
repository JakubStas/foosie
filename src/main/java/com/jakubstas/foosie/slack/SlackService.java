package com.jakubstas.foosie.slack;

import com.jakubstas.foosie.configuration.SlackProperties;
import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.service.model.User;
import com.jakubstas.foosie.slack.json.OpenChannelResponse;
import com.jakubstas.foosie.slack.json.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class SlackService {

    private final Logger logger = LoggerFactory.getLogger(SlackService.class);

    private final String openPrivateMessageChannelPath = "https://slack.com/api/im.open";

    private final String postPrivateMessagePath = "https://slack.com/api/chat.postMessage";

    private final String closePrivateMessageChannelPath = "https://slack.com/api/im.close";

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
        final String channelId = openPrivateMessageChannelAndReturnChannelId(player);
        postPrivateMessageToPlayerChannel(text, channelId);
        closePrivateMessageChannel(channelId);
    }

    private String openPrivateMessageChannelAndReturnChannelId(final User player) {
        logger.info("Opening private message channel to user: {}", player.getUserName());

        final String uriAsString = UriComponentsBuilder.fromUriString(openPrivateMessageChannelPath).queryParam("token", slackProperties.getAuthToken()).queryParam("user", player.getUserId()).queryParam("pretty", 1).build(true).toString();

        final URI uri = URI.create(uriAsString);

        logger.info("URI = " + uri.toString());

        final ResponseEntity<OpenChannelResponse> responseEntity = restTemplate.getForEntity(uri, OpenChannelResponse.class);
        final OpenChannelResponse openChannelResponse = responseEntity.getBody();

        return openChannelResponse.getChannel().getId();
    }

    private void postPrivateMessageToPlayerChannel(final String text, final String channelId) {
        logger.info("Posting private message to user channel: {}", channelId);

        final String uriAsString = UriComponentsBuilder.fromUriString(postPrivateMessagePath).queryParam("token", slackProperties.getAuthToken()).queryParam("channel", channelId).queryParam("text", text).queryParam("pretty", 1).build(true).toString();

        final URI uri = URI.create(uriAsString);

        logger.info("URI = " + uri.toString());
        logger.info("Message = " + text);

        restTemplate.getForEntity(uri, null);
    }

    private void closePrivateMessageChannel(final String channelId) {
        logger.info("Closing private message channel: {}", channelId);

        final String uriAsString = UriComponentsBuilder.fromUriString(closePrivateMessageChannelPath).queryParam("token", slackProperties.getAuthToken()).queryParam("channel", channelId).queryParam("pretty", 1).build(true).toString();

        final URI uri = URI.create(uriAsString);

        logger.info("URI = " + uri.toString());

        restTemplate.getForEntity(uri, null);
    }
}
