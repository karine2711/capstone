package com.aua.museum.booking.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class ValidEventTimeValidator implements ConstraintValidator<ValidEventTime, LocalTime> {
    int minHour = 10;
    int maxHour = 16;

    @Override
    public void initialize(ValidEventTime constraintAnnotation) {
        minHour = Integer.parseInt(constraintAnnotation.minHour());
        maxHour = Integer.parseInt(constraintAnnotation.maxHour());
    }

    @Override
    public boolean isValid(LocalTime time, ConstraintValidatorContext constraintValidatorContext) {
        LocalTime minTime = LocalTime.of(minHour, 0).minusMinutes(1);
        LocalTime maxTime = LocalTime.of(maxHour, 0).plusMinutes(1);

        return time.isAfter(minTime) && time.isBefore(maxTime);
    }


}
