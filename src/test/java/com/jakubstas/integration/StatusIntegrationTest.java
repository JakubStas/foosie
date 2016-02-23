package com.jakubstas.integration;

import com.jakubstas.integration.base.IntegrationTestBase;
import com.jakubstas.integration.util.RequestUtils;
import com.jakubstas.integration.util.SlashCommandUtils;
import com.jakubstas.integration.util.TestDataUtils;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockserver.model.HttpResponse.response;

/**
 * Note: There is no need to test other scenarios like one active game created, more active games created
 * or player joined game and lobby should be updated since status command is used in other integration tests
 * to validate results of other commands.
 */
public class StatusIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private TestDataUtils testDataUtils;

    @Test
    public void statusShouldReturnNoActiveGames() {
        // given
        final String hostName = testDataUtils.generateHostName();

        mockServerClient.when(requestUtils.getGamesCommandWithNoActiveGameRequest()).respond(response().withStatusCode(200));

        // when
        slashCommandUtils.slashGamesCommand(hostName);

        // then
        mockServerClient.verify(requestUtils.getGamesCommandWithNoActiveGameRequest(), VerificationTimes.exactly(1));

        mockServerClient.verify(requestUtils.getInternalErrorPrivateMessageBodyRequest(), VerificationTimes.exactly(0));
    }
}