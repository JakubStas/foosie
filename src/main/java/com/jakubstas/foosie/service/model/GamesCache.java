package com.jakubstas.foosie.service.model;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Validated
@Component
public class GamesCache {

    private Map<User, Game> activeGames = new ConcurrentHashMap<>();

    public void hostGame(final @Valid User host, final @Valid Game game) {
        activeGames.put(host, game);
    }

    public void cancelGameByHost(final String hostName) {
        final List<User> hosts = activeGames.keySet().stream().filter(user -> user.getUserName().equals(hostName)).collect(Collectors.toList());

        activeGames.remove(hosts.get(0));
    }

    public
    @Valid
    Game findByHostName(final String hostName) {
        final List<User> hosts = activeGames.keySet().stream().filter(user -> user.getUserName().equals(hostName)).collect(Collectors.toList());

        if (hosts.isEmpty()) {
            return null;
        } else {
            return activeGames.get(hosts.get(0));
        }
    }

    public int getNumberOfActiveGames() {
        return activeGames.size();
    }

    public Set<User> getSetOfHosts() {
        return activeGames.keySet();
    }
}
