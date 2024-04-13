package com.aua.museum.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidEventTimeValidator.class)
public @interface ValidEventTime {
    String message() default "{valid.event.time.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String minHour() default "10";

    String maxHour() default "16";
}
