package com.jakubstas.integration;

import com.jakubstas.integration.util.SlackMessageBodies;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class GameSetupIntegrationTest extends IntegrationTest {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Value("${slack.port}")
    private int slackPort;

    @Value("${slack.incoming-web-hook-path}")
    private String incomingWebHookPath;

    @Ignore
    @Test
    public void newGameInviteShouldBeCreated() {
        final String userName = "jakub";
        final String userId = "123";
        final String proposedTime = "12:00";
        final String responseUrl = "http://localhost:" + slackPort;

        // expect private Slack message stating that there are no active games
        mockServerClient.when(getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelvePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelveChannelMessageRequest(userName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(getNewGameLobyHasBeenCreatedPrivateMessageRequest(userName, proposedTime)).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(userName, responseUrl);

        // when
        slashCommandUtils.slashNewCommand(userName, userId, proposedTime, responseUrl);

        // then
        mockServerClient.verify(getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelvePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelveChannelMessageRequest(userName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameLobyHasBeenCreatedPrivateMessageRequest(userName, proposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game
        mockServerClient.when(getGamesCommandWithOneActiveGameRequest(userName, proposedTime, 1)).respond(response().withStatusCode(200));
        slashCommandUtils.slashGamesCommand(userName, responseUrl);
        mockServerClient.verify(getGamesCommandWithOneActiveGameRequest(userName, proposedTime, 1), VerificationTimes.exactly(1));
    }

    private HttpRequest getGamesCommandWithNoActiveGameRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createNoActiveGamesPrivateMessageBody());
    }

    private HttpRequest getGamesCommandWithOneActiveGameRequest(final String hostName, final String scheduledTime, final int numberOfPlayers) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createOneActiveGamePrivateMessageBody(hostName, scheduledTime, numberOfPlayers));
    }

    private HttpRequest getNewGameInviteAtTwelveChannelMessageRequest(final String hostName, final String proposedTime) {
        return request().withMethod("POST").withPath(incomingWebHookPath).withBody(SlackMessageBodies.createGameInviteChannelMessageBody(hostName, proposedTime));
    }

    private HttpRequest getNewGameInviteAtTwelvePrivateMessageRequest(final String proposedTime) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createGameInvitePrivateMessageBody(proposedTime));
    }

    private HttpRequest getNewGameLobyHasBeenCreatedPrivateMessageRequest(final String hostName, final String proposedTime) {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.createGameLobbyHasBeenCreatedPrivateMessageBody(hostName, proposedTime));
    }
}
