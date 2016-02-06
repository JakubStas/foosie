package com.jakubstas.foosie.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {

    private String gameMessageUrl;

    private List<String> playerIds = new ArrayList<>();

    private Date scheduledTime;

    public Game(final String userId, final String gameMessageUrl, final Date scheduledTime) {
        this.gameMessageUrl = gameMessageUrl;
        this.scheduledTime = scheduledTime;
    }

    public String getGameMessageUrl() {
        return gameMessageUrl;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }
}
