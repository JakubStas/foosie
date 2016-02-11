package com.jakubstas.foosie.validation;

import org.apache.commons.validator.UrlValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GameUrlValidator implements ConstraintValidator<GameUrl, String> {

    private final UrlValidator urlValidator = new UrlValidator(new String[]{"https"});

    @Override
    public void initialize(GameUrl gameUrl) {

    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        return urlValidator.isValid(url);
    }
}
