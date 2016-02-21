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

        createSingleGameExpectations(hostName, proposedTime);

        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));

        // when
        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTime);

        // then
        mockServerClient.verify(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(hostName, proposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there is one active game
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTime, 1)).respond(response().withStatusCode(200));

        // verify that new game has been created
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTime, 1), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void secondGameInviteShouldNotBeCreated() {
        final String hostName = testDataUtils.generateHostName();
        final String hostId = testDataUtils.generateUserId();
        final String proposedTimeFirstGame = testDataUtils.getProposedTimeInTenMinutes();
        final String proposedTimeSecondGame = testDataUtils.getProposedTimeInFuture(Calendar.MINUTE, 15);

        createSingleGameExpectations(hostName, proposedTimeFirstGame);
        // expect private Slack message stating that there are is a game hosted by you already
        mockServerClient.when(requestUtils.getGameInviteAlreadyPostedPrivateMessageRequest()).respond(response().withStatusCode(200));

        // check there are no games
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));

        // create first game invite
        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTimeFirstGame);

        // check there is one active game
        mockServerClient.when(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTimeFirstGame, 1)).respond(response().withStatusCode(200));
        slashCommandUtils.slashGamesCommand(hostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithOneActiveGameRequest(hostName, proposedTimeFirstGame, 1), VerificationTimes.exactly(1));

        // when
        slashCommandUtils.slashNewCommand(hostName, hostId, proposedTimeSecondGame);

        // then
        mockServerClient.verify(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTimeSecondGame), VerificationTimes.exactly(0));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(hostName, proposedTimeSecondGame), VerificationTimes.exactly(0));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(hostName, proposedTimeSecondGame), VerificationTimes.exactly(0));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }

    @Test
    @DirtiesContext
    public void twoGamesFromDifferentHostsScheduledForTheSameTimeShouldBeCreated() {
        final String firstHostName = testDataUtils.generateHostName(1);
        final String secondHostName = testDataUtils.generateHostName(2);
        final String firstHostId = testDataUtils.generateUserId();
        final String secondHostId = testDataUtils.generateUserId();
        final String proposedTime = testDataUtils.getProposedTimeInTenMinutes();

        createSingleGameExpectations(firstHostName, proposedTime);
        createSingleGameExpectations(secondHostName, proposedTime);

        slashCommandUtils.slashGamesCommand(firstHostName);
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));

        // when
        slashCommandUtils.slashNewCommand(firstHostName, firstHostId, proposedTime);
        slashCommandUtils.slashNewCommand(secondHostName, secondHostId, proposedTime);

        // then
        mockServerClient.verify(requestUtils.getNewGameInvitePrivateMessageRequest(proposedTime), VerificationTimes.exactly(2));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(firstHostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameInviteChannelMessageRequest(secondHostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(firstHostName, proposedTime), VerificationTimes.exactly(1));
        mockServerClient.verify(requestUtils.getNewGameLobyHasBeenCreatedPrivateMessageRequest(secondHostName, proposedTime), VerificationTimes.exactly(1));

        // expect private Slack message stating that there are two active game
        mockServerClient.when(requestUtils.getTwoActiveGamesAtTheSameTimePrivateMessageRequest(firstHostName, secondHostName, proposedTime, 1, 1)).respond(response().withStatusCode(200));

        // verify that new games have been created
        slashCommandUtils.slashGamesCommand(firstHostName);
        mockServerClient.verify(requestUtils.getTwoActiveGamesAtTheSameTimePrivateMessageRequest(firstHostName, secondHostName, proposedTime, 1, 1), VerificationTimes.exactly(1));

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
