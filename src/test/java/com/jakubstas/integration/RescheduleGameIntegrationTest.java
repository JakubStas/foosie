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

import java.util.Calendar;

import static org.mockserver.model.HttpResponse.response;

public class RescheduleGameIntegrationTest extends IntegrationTestBase {

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
    public void hostShouldBeAbleToRescheduleTheirOwnGame() {
        // given
        final String hostName = testDataUtils.generateHostName();
        final String hostId = testDataUtils.generateUserId();
        final String originalProposedTime = testDataUtils.getProposedTimeInTenMinutes();
        final String newProposedTime = testDataUtils.getProposedTimeInFuture(Calendar.MINUTE, 15);

        createSingleGameExpectations(hostName, originalProposedTime);

        slashCommandUtils.slashNewCommand(hostName, hostId, originalProposedTime);

        // expect private Slack message stating that there is one active game scheduled for original time
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, originalProposedTime, 1)).respond(response().withStatusCode(200));

        // verify that new game has been created and scheduled for original time
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, originalProposedTime, 1), VerificationTimes.exactly(1));

        // expect private Slack message about rescheduling newly created game
        mockServerClient.when(requestUtils.getSuccessfullyRescheduledGamePrivateMessageRequest(newProposedTime)).respond(response().withStatusCode(200));
        // expect channel Slack message about rescheduling newly created game
        mockServerClient.when(requestUtils.getCreateGameRescheduleChannelMessageRequest(hostName, newProposedTime)).respond(response().withStatusCode(200));

        // when
        slashCommandUtils.slashRescheduleCommand(hostName, newProposedTime);

        // then
        mockServerClient.verify(requestUtils.getCreateGameRescheduleChannelMessageRequest(hostName, newProposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getSuccessfullyRescheduledGamePrivateMessageRequest(newProposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game scheduled for new time
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, newProposedTime, 1)).respond(response().withStatusCode(200));

        // verify that new game has been created and scheduled for new time
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, newProposedTime, 1), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void hostShouldNotBeAbleToRescheduleOtherGame() {
        // given
        final String originalHostName = testDataUtils.generateHostName();
        final String originalHostId = testDataUtils.generateUserId();
        final String originalProposedTime = testDataUtils.getProposedTimeInTenMinutes();
        final String newProposedTime = testDataUtils.getProposedTimeInFuture(Calendar.MINUTE, 15);
        final String otherHostName = testDataUtils.generateHostName();

        createSingleGameExpectations(originalHostName, originalProposedTime);

        slashCommandUtils.slashNewCommand(originalHostName, originalHostId, originalProposedTime);

        // expect private Slack message stating that there is one active game scheduled for original time
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(originalHostName, originalProposedTime, 1)).respond(response().withStatusCode(200));

        // verify that new game has been created and scheduled for original time
        slashCommandUtils.slashGamesCommand(originalHostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(originalHostName, originalProposedTime, 1), VerificationTimes.exactly(1));

        // expect private Slack message about rescheduling newly created game
        mockServerClient.when(requestUtils.getUnableToRescheduleGamePrivateMessageRequest()).respond(response().withStatusCode(200));

        // when
        slashCommandUtils.slashRescheduleCommand(otherHostName, newProposedTime);

        // then
        mockServerClient.verify(requestUtils.getUnableToRescheduleGamePrivateMessageRequest(), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game scheduled for original time
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(originalHostName, originalProposedTime, 1)).respond(response().withStatusCode(200));

        // verify that the game has not been scheduled
        slashCommandUtils.slashGamesCommand(originalHostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(originalHostName, originalProposedTime, 1), VerificationTimes.exactly(2));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void hostShouldNotBeAbleToRescheduleNonExistingGame() {
        // given
        final String hostName = testDataUtils.generateHostName();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        // expect private Slack message about unability of rescheduling the game
        mockServerClient.when(requestUtils.getUnableToRescheduleGamePrivateMessageRequest()).respond(response().withStatusCode(200));

        // when
        slashCommandUtils.slashRescheduleCommand(hostName, proposedTime);

        // then
        mockServerClient.verify(requestUtils.getUnableToRescheduleGamePrivateMessageRequest(), VerificationTimes.exactly(1));

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
