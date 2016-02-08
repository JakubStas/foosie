package com.jakubstas.integration;

import com.jakubstas.foosie.FoosieApplication;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({ FoosieApplication.class, IntegrationTestsConfiguration.class })
@WebIntegrationTest({"server.port=8081"})
public class GameSetupIntegrationTest {

    @Autowired
    private SlashCommandUtils slashCommandUtils;

    @Autowired
    private MockServerClient mockServerClient;

    @Value("${slack.port}")
    private int slackPort;

    @Test
    public void itShouldSetupNewGame() {
        // given
        final String userName = "jakub";
        final String proposedTime = "12:00";
        final String responseUrl = "localhost:" + slackPort;

        mockServerClient.when(request().withMethod("POST")).respond(response().withStatusCode(200));

        slashCommandUtils.slashGamesCommand(userName, responseUrl);

        // when
        slashCommandUtils.slashNewCommand(userName, proposedTime, responseUrl);

        // then

        mockServerClient.verify(request().withMethod("POST"), VerificationTimes.exactly(1));
    }
}
