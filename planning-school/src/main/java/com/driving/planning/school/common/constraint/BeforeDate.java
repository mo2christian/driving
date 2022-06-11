package com.driving.planning.school.common.constraint;

import com.driving.planning.school.common.TimeConstants;
import com.driving.planning.school.common.constraint.validator.BeforeDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BeforeDateValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeDate {

    String beforeField();
    String afterField();
    boolean allowEqual() default false;
    TimeConstants dateFormat() default TimeConstants.DATE_FORMAT;
    String message() default "les champs de date doivent inferieurs ou egale les unes par rapport aux autres";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
