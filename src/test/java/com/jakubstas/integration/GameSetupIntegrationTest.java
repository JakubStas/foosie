package com.jakubstas.integration;

import com.jakubstas.integration.base.IntegrationTestBase;
import com.jakubstas.integration.util.RequestUtils;
import com.jakubstas.integration.util.SlashCommandUtils;
import com.jakubstas.integration.util.TestDataUtils;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.mockserver.model.HttpResponse.response;

public class GameSetupIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    @DirtiesContext
    public void newGameInviteShouldBeCreated() {
        final String hostName = testDataUtils.generateHostName();
        final String hostId = testDataUtils.generateUserId();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        // expect private Slack message stating that there are no active games
        mockServerClient.when(requestUtils.getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(hostName);

        // when
        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime);

        // then
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTime, 1)).respond(response().withStatusCode(200));
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTime, 1), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }
}
