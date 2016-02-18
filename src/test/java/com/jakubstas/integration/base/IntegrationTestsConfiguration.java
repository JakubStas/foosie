package com.jakubstas.integration.base;

import com.jakubstas.integration.util.RequestUtils;
import com.jakubstas.integration.util.SlashCommandUtils;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(locations = "application.properties")
public class IntegrationTestsConfiguration {

    @Value("${slack.port}")
    private int slackPort;

    @Bean
    public SlashCommandUtils slashCommandUtils() {
        return new SlashCommandUtils();
    }

    @Bean
    public RequestUtils requestUtils() {
        return new RequestUtils();
    }

    @Bean
    public ClientAndServer clientAndServer() {
        return new ClientAndServer(slackPort);
    }
}
