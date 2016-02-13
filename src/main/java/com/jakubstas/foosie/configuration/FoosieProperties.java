package com.jakubstas.foosie.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "foosie")
public class FoosieProperties {

    private int scheduleBefore;

    public int getScheduleBefore() {
        return scheduleBefore;
    }

    public void setScheduleBefore(int scheduleBefore) {
        this.scheduleBefore = scheduleBefore;
    }
}
