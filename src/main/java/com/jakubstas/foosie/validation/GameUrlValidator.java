package com.jakubstas.foosie.validation;

import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GameUrlValidator implements ConstraintValidator<GameUrl, String> {

    private final URLValidator urlValidator = new URLValidator();

    @Override
    public void initialize(GameUrl gameUrl) {

    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        return urlValidator.isValid(url, constraintValidatorContext);
    }
}
