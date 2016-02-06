package com.jakubstas;

import com.jakubstas.foosie.FoosieApplication;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(FoosieApplication.class)
@WebIntegrationTest({"server.port=8081"})
public class GameSetupIntegartionTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private MockServerClient mockServerClient;

    private final RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void itShouldSetupNewGame() {
        // given
        final String token = "abc123";
        final String userName = "jakub";
        final String text = "12:00";
        final String responseUrl = "localhost";

        // when
        final String url = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(8081).queryParam("token", token).queryParam("user_name", userName).queryParam("text", text).queryParam("response_url", responseUrl).build().toString();

        final URI uri = restTemplate.postForLocation(url, null);

        // then
    }
}
