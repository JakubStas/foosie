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

public class CancelGameIntegrationTest extends IntegrationTestBase {

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
    public void hostShouldBeAbleToCancelTheirOwnGame() {
        // given
        final String hostName = testDataUtils.generateHostName();
        final String hostId = testDataUtils.generateUserId();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        createSingleGameExpectations(hostName, proposedTime);

        // expect private Slack message about canceling newly created game
        mockServerClient.when(requestUtils.getSuccessfullyCanceledGamePrivateMessageRequest()).respond(response().withStatusCode(200));
        // expect channel Slack message about canceling newly created game
        mockServerClient.when(requestUtils.getCreateGameCanceledChannelMessageRequest(hostName)).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime);

        // when
        slashCommandUtils.slashCancelCommand(hostName);

        // then
        mockServerClient.verify(requestUtils.getCreateGameCanceledChannelMessageRequest(hostName), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getSuccessfullyCanceledGamePrivateMessageRequest(), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void hostShouldNotBeAbleToCancelOtherGame() {
        // given
        final String otherHostName = testDataUtils.generateHostName();
        final String originalHostName = testDataUtils.generateHostName();
        final String originalHostId = testDataUtils.generateUserId();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        createSingleGameExpectations(originalHostName, proposedTime);

        // expect private Slack message about canceling newly created game
        mockServerClient.when(requestUtils.getUnableToCancelGamePrivateMessageRequest()).respond(response().withStatusCode(200));

        slashCommandUtils.slashNewCommand(originalHostName, originalHostId, proposedTime);

        // when
        slashCommandUtils.slashCancelCommand(otherHostName);

        // then
        mockServerClient.verify(requestUtils.getUnableToCancelGamePrivateMessageRequest(), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void hostShouldNotBeAbleToCancelNonExistingGame() {
        // given
        final String hostName = testDataUtils.generateHostName();

        // expect private Slack message about unability newly created game
        mockServerClient.when(requestUtils.getUnableToCancelGamePrivateMessageRequest()).respond(response().withStatusCode(200));

        // when
        slashCommandUtils.slashCancelCommand(hostName);

        // then
        mockServerClient.verify(requestUtils.getUnableToCancelGamePrivateMessageRequest(), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    private void createSingleGameExpectations(final String hostName, final String proposedTime) {
        // expect private Slack message stating that there are no active games
        mockServerClient.when(requestUtils.getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));
        // expect private Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime)).respond(response().withStatusCode(200));
        // expect public Slack message about newly created game
        mockServerClient.when(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
        // expect private Slack message displaying lobby status
        mockServerClient.when(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime)).respond(response().withStatusCode(200));
    }
}
