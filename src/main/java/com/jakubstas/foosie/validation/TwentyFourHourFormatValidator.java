package com.jakubstas.foosie.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwentyFourHourFormatValidator implements ConstraintValidator<TodayButFuture, String> {

    private final Pattern timePattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");

    @Override
    public void initialize(TodayButFuture constraintAnnotation) {

    }

    @Override
    public boolean isValid(String proposedTime, ConstraintValidatorContext context) {
        final Matcher matcher = timePattern.matcher(proposedTime);

        return matcher.matches();
    }
}
