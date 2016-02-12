package com.jakubstas.foosie.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for 24-hour format of time as string.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TwentyFourHourFormatValidator.class)
@Documented
public @interface TwentyFourHourFormat {

    String message() default "Provided time must be in 24-hour format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
