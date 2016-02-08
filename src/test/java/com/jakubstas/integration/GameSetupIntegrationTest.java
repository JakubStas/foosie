package com.jakubstas.integration;

import com.jakubstas.foosie.FoosieApplication;
import com.jakubstas.integration.util.SlackMessageBodies;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({FoosieApplication.class, IntegrationTestsConfiguration.class})
@WebIntegrationTest({"server.port=8081"})
public class GameSetupIntegrationTest {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Value("${slack.port}")
    private int slackPort;

    @Value("${slack.incoming-web-hook-path}")
    private String incomingWebHookPath;

    @Test
    public void itShouldSetupNewGame() {
        // given
        final String userName = "jakub";
        final String proposedTime = "12:00";
        final String responseUrl = "http://localhost:" + slackPort;

        // expect private Slack message stating that there are no active games
        mockServerClient.when(getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelvePrivateMessageRequest()).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(getNewGameInviteAtTwelveChannelMessageRequest()).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(userName, responseUrl);

        // when
        slashCommandUtils.slashNewCommand(userName, proposedTime, responseUrl);

        // then
        mockServerClient.verify(getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelvePrivateMessageRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(getNewGameInviteAtTwelveChannelMessageRequest(), VerificationTimes.exactly(1));

        // expect private Slack message stating that there are no active games
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
}
