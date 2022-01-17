package com.driving.planning.school.common.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = { })
@NotBlank
@Pattern(regexp = "[0-9]{9,10}", message = "doit etre un numéro valide")
public @interface PhoneNumber {

    String message() default "Invalid phone number";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
