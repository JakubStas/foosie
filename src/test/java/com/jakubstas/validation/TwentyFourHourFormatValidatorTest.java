package com.jakubstas.validation;


import com.jakubstas.foosie.validation.TwentyFourHourFormatValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TwentyFourHourFormatValidatorTest {

    private final TwentyFourHourFormatValidator validator = new TwentyFourHourFormatValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void validTimesShouldPass() {
        final String midnight = "00:00";
        final String fiveMinutesAfterMidnight = "00:05";
        final String sevenMinutesAfterMidnight = "0:07";
        final String fiveMinutesAfterOne = "01:05";
        final String sevenMinutesAfterOne = "1:07";
        final String tenMinutesAfterOne = "01:10";
        final String fifteenMinutesAfterOne = "01:15";
        final String seventeenMinutesAfterOne = "1:17";
        final String quarterPastTen = "10:15";
        final String quarterPastTwelve = "12:15";

        assertTrue(validator.isValid(midnight, constraintValidatorContext));
        assertTrue(validator.isValid(fiveMinutesAfterMidnight, constraintValidatorContext));
        assertTrue(validator.isValid(sevenMinutesAfterMidnight, constraintValidatorContext));
        assertTrue(validator.isValid(fiveMinutesAfterOne, constraintValidatorContext));
        assertTrue(validator.isValid(sevenMinutesAfterOne, constraintValidatorContext));
        assertTrue(validator.isValid(tenMinutesAfterOne, constraintValidatorContext));
        assertTrue(validator.isValid(fifteenMinutesAfterOne, constraintValidatorContext));
        assertTrue(validator.isValid(seventeenMinutesAfterOne, constraintValidatorContext));
        assertTrue(validator.isValid(quarterPastTen, constraintValidatorContext));
        assertTrue(validator.isValid(quarterPastTwelve, constraintValidatorContext));
    }

    @Test
    public void invalidTimesShouldFail() {
        final String invalidMidnight = "24:00";
        final String tenMinutesAfterOne = "01:1";
        final String quarterPastTen = "24:15";
        final String quarterPastTwelve = "12:65";

        assertFalse(validator.isValid(invalidMidnight, constraintValidatorContext));
        assertFalse(validator.isValid(tenMinutesAfterOne, constraintValidatorContext));
        assertFalse(validator.isValid(quarterPastTen, constraintValidatorContext));
        assertFalse(validator.isValid(quarterPastTwelve, constraintValidatorContext));
    }
}
