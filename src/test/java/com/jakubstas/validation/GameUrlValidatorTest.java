package com.jakubstas.validation;

import com.jakubstas.foosie.validation.GameUrlValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GameUrlValidatorTest {

    private final GameUrlValidator validator = new GameUrlValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void httpsUrlShouldPass() {
        final String url = "https://www.slack.com/random";

        assertTrue(validator.isValid(url, constraintValidatorContext));
    }

    @Test
    public void nonHttpsUrlsShouldFail() {
        final String httpUrl = "http://www.slack.com/random";
        final String ftpUrl = "ftp://www.slack.com/random";
        final String incompleteUrl = "www.slack.com/random";
        final String invalidUrl = "////123.slack.com/random";

        assertFalse(validator.isValid(httpUrl, constraintValidatorContext));
        assertFalse(validator.isValid(ftpUrl, constraintValidatorContext));
        assertFalse(validator.isValid(incompleteUrl, constraintValidatorContext));
        assertFalse(validator.isValid(invalidUrl, constraintValidatorContext));
    }
}
