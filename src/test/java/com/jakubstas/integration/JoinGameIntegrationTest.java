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

import java.util.Optional;

import static org.mockserver.model.HttpResponse.response;

public class JoinGameIntegrationTest extends IntegrationTestBase {

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
    public void userShouldJoinNewGameWithoutSpecifyingHostName() {
        // given
        final String hostName = testDataUtils.generateHostName();
        final String hostId = testDataUtils.generateUserId();
        final String playerName = testDataUtils.generatePlayerName();
        final String playerId = testDataUtils.generateUserId();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        // expect private Slack message about newly created game
        mockServerClient.when(requestUtils.getGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(requestUtils.getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));

        // expect private Slack message about joining newly created game
        mockServerClient.when(requestUtils.getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message about new client joining the game
        mockServerClient.when(requestUtils.getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(playerName)).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime);

        mockServerClient.verify(requestUtils.getGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));

        // when
        slashCommandUtils.slashIaminCommand(playerName, playerId, Optional.empty());

        // then
        mockServerClient.verify(requestUtils.getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(playerName), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }
}
