package com.jakubstas.integration;

import com.jakubstas.integration.util.SlashCommandUtils;
import org.mockserver.client.proxy.ProxyClient;
import org.mockserver.client.server.MockServerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestsConfiguration {

    @Value("${slack.port}")
    private int slackPort;

    @Bean
    public SlashCommandUtils slashCommandUtils() {
        return new SlashCommandUtils();
    }

    @Bean
    public MockServerClient mockServerClient() {
        return new MockServerClient("localhost", slackPort);
    }
}
