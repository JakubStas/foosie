package com.jakubstas.validation;

import com.jakubstas.foosie.validation.TodayButFutureValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TodayButFutureValidatorTest {

    private final TodayButFutureValidator validator = new TodayButFutureValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void nullDateShouldFail() {
        assertDateThatShouldFailValidation(null);
    }

    @Test
    public void dateOfTestRunShouldFail() {
        assertDateThatShouldFailValidation(Calendar.getInstance().getTime());
    }

    @Test
    public void dateOneMinuteInPastTodayShouldFail() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);

        assertDateThatShouldFailValidation(calendar.getTime());
    }

    @Test
    public void dateOneHourInPastTodayShouldFail() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        assertDateThatShouldFailValidation(calendar.getTime());
    }

    @Test
    public void dateOneDayInPastShouldFail() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        assertDateThatShouldFailValidation(calendar.getTime());
    }


    @Test
    public void datesOneMinuteInTheFutureTodayShouldPass() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);

        assertDateThatShouldPassValidation(calendar.getTime());
    }

    @Test
    public void datesOneHourInTheFutureTodayShouldPass() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        assertDateThatShouldPassValidation(calendar.getTime());
    }

    @Test
    public void datesOneDayInTheFutureTodayShouldFail() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        assertDateThatShouldFailValidation(calendar.getTime());
    }

    private final void assertDateThatShouldFailValidation(final Date date) {
        assertFalse(validator.isValid(date, constraintValidatorContext));
    }

    private final void assertDateThatShouldPassValidation(final Date date) {
        assertTrue(validator.isValid(date, constraintValidatorContext));
    }
}
