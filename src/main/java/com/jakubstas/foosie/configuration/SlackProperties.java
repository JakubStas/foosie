package com.jakubstas.foosie.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    private String incomingWebHookUri;

    private String iaminCommandToken;

    private String newCommandToken;

    private String updateCommandToken;

    private String cancelCommandToken;

    public String getIncomingWebHookUri() {
        return incomingWebHookUri;
    }

    public void setIncomingWebHookUri(String incomingWebHookUri) {
        this.incomingWebHookUri = incomingWebHookUri;
    }

    public String getIaminCommandToken() {
        return iaminCommandToken;
    }

    public void setIaminCommandToken(String iaminCommandToken) {
        this.iaminCommandToken = iaminCommandToken;
    }

    public String getNewCommandToken() {
        return newCommandToken;
    }

    public void setNewCommandToken(String newCommandToken) {
        this.newCommandToken = newCommandToken;
    }

    public String getCancelCommandToken() {
        return cancelCommandToken;
    }

    public void setCancelCommandToken(String cancelCommandToken) {
        this.cancelCommandToken = cancelCommandToken;
    }

    public String getUpdateCommandToken() {
        return updateCommandToken;
    }

    public void setUpdateCommandToken(String updateCommandToken) {
        this.updateCommandToken = updateCommandToken;
    }
}