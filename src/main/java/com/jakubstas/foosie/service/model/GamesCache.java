package com.jakubstas.foosie.service.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public @Valid Game findByHostName(final String hostName) {
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

    public List<User> getSetOfHosts() {
        final ArrayList<User> hosts = Lists.newArrayList(activeGames.keySet());

        hosts.sort(new Ordering<User>() {
            @Override
            public int compare(@Nullable User left, @Nullable User right) {
                return left.getUserName().compareTo(right.getUserName());
            }
        });

        return ImmutableList.copyOf(hosts);
    }
}
