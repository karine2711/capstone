package com.aua.museum.booking.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@NotBlank
@Size(min = 2, max = 25)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ValidSecurityQuestionAnswer {
    String message() default "{valid.security.question.answer.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}