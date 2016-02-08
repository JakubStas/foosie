package com.jakubstas.integration;

import com.jakubstas.integration.util.SlashCommandUtils;
import org.mockserver.integration.ClientAndServer;
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
    public ClientAndServer clientAndServer() {
        return new ClientAndServer(slackPort);
    }
}
