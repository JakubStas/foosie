package com.jakubstas.foosie.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class TodayButFutureValidator implements ConstraintValidator<TodayButFuture, Date> {

    private final ZoneId zoneId = ZoneId.of("Europe/Dublin");


    @Override
    public void initialize(TodayButFuture todayButFuture) {

    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return false;
        }

        final boolean isAfterYesterdayMidnight = date.after(getYesterdayMidnight());
        final boolean isBeforeTodayMidnight = date.before(getTodayMidnight());

        if (isAfterYesterdayMidnight && isBeforeTodayMidnight && date.after(new Date())) {
            return true;
        }

        return false;
    }

    private Date getTodayMidnight() {
        return getDateInstance(getYesterdayMidnightInternal().plusDays(1));
    }

    private Date getYesterdayMidnight() {
        return getDateInstance(getYesterdayMidnightInternal());
    }

    private LocalDateTime getYesterdayMidnightInternal() {
        final LocalTime midnight = LocalTime.MIDNIGHT;
        final LocalDate today = LocalDate.now(zoneId);

        return LocalDateTime.of(today, midnight);
    }

    private Date getDateInstance(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }
}
