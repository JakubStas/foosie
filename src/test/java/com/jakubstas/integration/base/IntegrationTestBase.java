package com.jakubstas.integration.base;

import com.jakubstas.foosie.FoosieApplication;
import org.junit.After;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({FoosieApplication.class, IntegrationTestsConfiguration.class})
@WebIntegrationTest({"server.port=8081"})
public abstract class IntegrationTestBase {

    @Autowired
    private MockServerClient mockServerClient;

    @After
    public void clearAllExpectations() {
        mockServerClient.reset();
    }
}
