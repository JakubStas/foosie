package com.jakubstas.foosie.service.model;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Validated
@Component
public class GamesCache {

    private Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public void addGame(final String userName, final @Valid Game game) {
        activeGames.put(userName, game);
    }

    public void cancelGameByHost(final String hostName) {
        activeGames.remove(hostName);
    }

    public @Valid Game findByHostName(final String hostName) {
        return activeGames.get(hostName);
    }

    public int getNumberOfActiveGames() {
        return activeGames.size();
    }

    public Set<String> getSetOfHostNames() {
        return activeGames.keySet();
    }
}
