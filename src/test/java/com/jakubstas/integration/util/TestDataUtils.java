package com.jakubstas.integration.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TestDataUtils {

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public final String getProposedTimeInTenMinutes() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        return sdf.format(cal.getTime());
    }

    public final String getProposedTimeInFuture(final int unit, final int number) {
        final Calendar cal = Calendar.getInstance();
        cal.add(unit, number);

        return sdf.format(cal.getTime());
    }

    public final String generateHostName() {
        return "host_" + generateUserId();
    }

    public final String generateHostName(final int i) {
        return "host_" + i;
    }

    public final String generatePlayerName() {
        return "player_" + new Date().getTime();
    }

    public final String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
