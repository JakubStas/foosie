package com.jakubstas.foosie.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for date that is in the future relative to current time yet in the same day.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TodayButFutureValidator.class)
@Documented
public @interface TodayButFuture {

    String message() default "Provided time must be in the future but still today!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
