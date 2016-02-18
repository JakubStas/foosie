package com.jakubstas.integration;

import com.jakubstas.integration.util.SlackMessageBodies;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Test
    public void userShouldJoinNewGameWithoutSpecifyingHostName() {
        // given
        final String hostName = "jakub";
        final String hostId = "123";
        final String userName = "karol";
        final String userId = "456";
        final String proposedTime = getProposedTime();
        final String responseUrl = "http://localhost:" + slackPort;

        // expect private Slack message about newly created game
        mockServerClient.when(getGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelveChannelMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));

        // expect private Slack message about joining newly created game
        mockServerClient.when(getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message about new client joining the game
        mockServerClient.when(getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(userName)).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime, responseUrl);

        // when
        slashCommandUtils.slashIaminCommand(userName, userId, Optional.empty(), responseUrl);

        // then
        mockServerClient.verify(getGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelveChannelMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(userName), VerificationTimes.exactly(1));
    }

    private String getProposedTime() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        return sdf.format(cal.getTime());
    }

    private HttpRequest getNewGameInviteAtTwelveChannelMessageRequest(final String hostName, final String proposedTime) {
        return request().withMethod("POST").withPath(incomingWebHookPath).withBody(SlackMessageBodies.createGameInviteChannelMessageBody(hostName, proposedTime));
    }

    private HttpRequest getGameInvitePrivateMessageRequest(final String proposedTime) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createGameInvitePrivateMessageBody(proposedTime));
    }

    private HttpRequest getNewGameLobbyHasBeenCreatedPrivateMessageRequest(final String hostName, final String proposedTime) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createGameLobbyHasBeenCreatedPrivateMessageBody(hostName, proposedTime));
    }

    private HttpRequest getConfirmationAboutJoiningNewGamePrivateMessageRequest(final String hostName, final String proposedTime) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createConfirmationAboutJoiningGamePrivateMessageBody(hostName, proposedTime));
    }

    private HttpRequest getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(final String userName) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createNewPlayerJoinedGameNotificationPrivateMessageBody(userName));
    }
}
