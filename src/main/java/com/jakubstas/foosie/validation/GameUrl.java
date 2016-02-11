package com.jakubstas.foosie.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for URL strings.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GameUrlValidator.class)
@Documented
public @interface GameUrl {

    String message() default "Game URL must be a valid https URL!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
