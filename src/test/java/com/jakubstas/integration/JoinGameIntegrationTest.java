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
        final String hostId = "123";
        final String userName = testDataUtils.generatePlayerName();
        final String userId = "456";
        final String proposedTime = getProposedTime();

        // expect private Slack message about newly created game
        mockServerClient.when(requestUtils.getGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(requestUtils.getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));

        // expect private Slack message about joining newly created game
        mockServerClient.when(requestUtils.getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message about new client joining the game
        mockServerClient.when(requestUtils.getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(userName)).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime);

        // when
        slashCommandUtils.slashIaminCommand(userName, userId, Optional.empty());

        // then
        mockServerClient.verify(requestUtils.getGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobbyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getConfirmationAboutJoiningNewGamePrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNotificationThatNewPlayerJoinedGamePrivateMessageRequest(userName), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }
}
