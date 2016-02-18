package com.jakubstas.integration.util;

import java.util.Date;

public class TestDataUtils {

    public final String generateHostName() {
        return "host_" + new Date().getTime();
    }

    public final String generatePlayerName() {
        return "player_" + new Date().getTime();
    }
}
