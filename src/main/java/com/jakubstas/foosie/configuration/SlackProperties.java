package com.jakubstas.foosie.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    private String incomingWebHookUri;

    private String slashCommandToken;

    public String getIncomingWebHookUri() {
        return incomingWebHookUri;
    }

    public void setIncomingWebHookUri(String incomingWebHookUri) {
        this.incomingWebHookUri = incomingWebHookUri;
    }

    public String getSlashCommandToken() {
        return slashCommandToken;
    }

    public void setSlashCommandToken(String slashCommandToken) {
        this.slashCommandToken = slashCommandToken;
    }
}