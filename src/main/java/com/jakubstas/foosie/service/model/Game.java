package com.jakubstas.foosie.service.model;

import com.jakubstas.foosie.validation.TodayButFuture;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {

    private String gameMessageUrl;

    private User host;

    @Size(min = 0, max = 3)
    private List<User> players = new ArrayList<>();

    @TodayButFuture
    private Date scheduledTime;

    public Game(final User player, final String gameMessageUrl, final Date scheduledTime) {
        this.gameMessageUrl = gameMessageUrl;
        this.scheduledTime = scheduledTime;
        this.host = player;
    }

    public String getGameMessageUrl() {
        return gameMessageUrl;
    }

    public List<User> getPlayers() {
        return players;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void reschedule(final Date newScheduledTime) {
        this.scheduledTime = newScheduledTime;
    }

    public User getHost() {
        return host;
    }

    public void join(final User player) {
        players.add(player);
    }

    public boolean isReady() {
        return players.size() == 3;
    }

    public Optional<User> getPlayerByName(final String userName) {
        final List<User> player = players.stream().filter(user -> user.getUserName().equals(userName)).collect(Collectors.toList());

        if (player.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(player.get(0));
        }
    }
}
