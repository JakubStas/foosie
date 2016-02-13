package com.jakubstas.foosie.slack.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackMessage {

    @JsonProperty
    private final String text;

    public SlackMessage(String text) {
        this.text = text;
    }
}
