package com.driving.planning.school.common.constraint;

import com.driving.planning.school.common.constraint.validator.WorkDayFormValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkDayFormValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkDay {
    String message() default "doit avoir des temps de début et de fin valides";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
