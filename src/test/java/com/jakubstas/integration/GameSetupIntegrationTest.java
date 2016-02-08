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
        final String proposedTime = "12:00";
        final String responseUrl = "http://localhost:" + slackPort;

        // expect private Slack message stating that there are no active games
        mockServerClient.when(getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelvePrivateMessageRequest()).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelveChannelMessageRequest()).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(getNewGameLobyHasBeenCreatedPrivateMessageRequest()).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(userName, responseUrl);

        // when
        slashCommandUtils.slashNewCommand(userName, proposedTime, responseUrl);

        // then
        mockServerClient.verify(getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelvePrivateMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelveChannelMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameLobyHasBeenCreatedPrivateMessageRequest(), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game
        mockServerClient.when(getGamesCommandWithOneActiveGameRequest()).respond(response().withStatusCode(200));
        slashCommandUtils.slashGamesCommand(userName, responseUrl);
        mockServerClient.verify(getGamesCommandWithOneActiveGameRequest(), VerificationTimes.exactly(1));
    }

    private HttpRequest getGamesCommandWithNoActiveGameRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.noActiveGamesPrivateMessageBody);
    }

    private HttpRequest getGamesCommandWithOneActiveGameRequest() {
        return request().withMethod("POST").withPath("/").withBody(SlackMessageBodies.oneActiveGamePrivateMessageBody);
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
}
