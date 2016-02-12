package com.jakubstas.foosie.service.model;

import com.jakubstas.foosie.validation.TodayButFuture;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {

    private String gameMessageUrl;

    private List<String> playerIds = new ArrayList<>();

    @TodayButFuture
    private Date scheduledTime;

    public Game(final String userId, final String gameMessageUrl, final Date scheduledTime) {
        this.gameMessageUrl = gameMessageUrl;
        this.scheduledTime = scheduledTime;

        playerIds.add(userId);
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

    public void reschedule(final Date newScheduledTime) {
        this.scheduledTime = newScheduledTime;
    }
}
