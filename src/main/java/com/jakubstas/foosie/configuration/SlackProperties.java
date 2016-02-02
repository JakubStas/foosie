package com.jakubstas.foosie.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    private String incomingWebhook;

    private String outgoinggWebhook;

    public String getIncomingWebhook() {
        return incomingWebhook;
    }

    public void setIncomingWebhook(String incomingWebhook) {
        this.incomingWebhook = incomingWebhook;
    }

    public String getOutgoinggWebhook() {
        return outgoinggWebhook;
    }

    public void setOutgoinggWebhook(String outgoinggWebhook) {
        this.outgoinggWebhook = outgoinggWebhook;
    }
}
