package com.jakubstas.foosie.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

public class TwentyFourHourFormatValidator implements ConstraintValidator<TodayButFuture, Date> {
    @Override
    public void initialize(TodayButFuture constraintAnnotation) {

    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        return false;
    }
}
