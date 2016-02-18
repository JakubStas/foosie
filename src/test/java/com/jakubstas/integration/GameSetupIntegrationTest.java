package com.jakubstas.integration;

import com.jakubstas.integration.base.IntegrationTestBase;
import com.jakubstas.integration.util.RequestUtils;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockserver.model.HttpResponse.response;

public class GameSetupIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Autowired
    private RequestUtils requestUtils;

    @Test
    public void newGameInviteShouldBeCreated() {
        final String userName = "jakub";
        final String userId = "123";
        final String proposedTime = getProposedTime();

        // expect private Slack message stating that there are no active games
        mockServerClient.when(requestUtils.getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInviteChannelMessageRequest(userName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(userName, proposedTime)).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(userName);

        // when
        slashCommandUtils.slashNewCommand(userName, userId, proposedTime);

        // then
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(userName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(userName, proposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(userName, proposedTime, 1)).respond(response().withStatusCode(200));
        slashCommandUtils.slashGamesCommand(userName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(userName, proposedTime, 1), VerificationTimes.exactly(1));
    }
}
