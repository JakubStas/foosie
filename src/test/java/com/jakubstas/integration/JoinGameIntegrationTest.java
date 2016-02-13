package com.jakubstas.integration;

import com.jakubstas.integration.util.SlackMessageBodies;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JoinGameIntegrationTest extends IntegrationTest {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Value("${slack.port}")
    private int slackPort;

    @Value("${slack.incoming-web-hook-path}")
    private String incomingWebHookPath;

    @Test
    public void userShouldJoinNewGameWithoutSpecifyingHostName() {
        // given
        final String hostName = "jakub";
        final String hostId = "123";
        final String userName = "karol";
        final String userId = "456";
        final String proposedTime = "12:00";
        final String responseUrl = "http://localhost:" + slackPort;

        // expect private Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelvePrivateMessageRequest()).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelveChannelMessageRequest()).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(getNewGameLobyHasBeenCreatedPrivateMessageRequest()).respond(response().withStatusCode(200));

        // expect private Slack message about joining newly created game
        mockServerClient.when(getConfirmationAboutJoiningNewGamePrivateMessageRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about new client joining the game
        mockServerClient.when(getNotificationThatNewPlayerJoinedGamePrivateMessageRequest()).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime, responseUrl);

        // when
        slashCommandUtils.slashIaminCommand(userName, userId, Optional.empty(), responseUrl);

        // then
        mockServerClient.verify(getNewGameInviteAtTwelvePrivateMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelveChannelMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameLobyHasBeenCreatedPrivateMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getConfirmationAboutJoiningNewGamePrivateMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(), VerificationTimes.exactly(1));
    }

    private HttpRequest getNewGameInviteAtTwelveChannelMessageRequest() {
        return request().withMethod("POST").withPath(incomingWebHookPath).withBody(SlackMessageBodies.gameInviteAtTwelvePostedChannelMessageBody);
    }

    private HttpRequest getNewGameInviteAtTwelvePrivateMessageRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.gameInviteAtTwelvePostedPrivateMessageBody);
    }

    private HttpRequest getNewGameLobyHasBeenCreatedPrivateMessageRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.gameLobbyHasBeenCreatedPrivateMessageBody);
    }

    private HttpRequest getConfirmationAboutJoiningNewGamePrivateMessageRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.confirmationAboutJoiningGamePrivateMessageBody);
    }

    private HttpRequest getNotificationThatNewPlayerJoinedGamePrivateMessageRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createNewPlayerJoinedGameNotificationPrivateMessageBody("karol"));
    }
}
