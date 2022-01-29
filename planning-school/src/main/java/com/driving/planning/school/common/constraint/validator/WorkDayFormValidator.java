package com.driving.planning.school.common.constraint.validator;

import com.driving.planning.school.common.constraint.WorkDay;
import com.driving.planning.school.common.form.WorkDayForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkDayFormValidator implements ConstraintValidator<WorkDay, WorkDayForm> {

    @Override
    public boolean isValid(WorkDayForm value, ConstraintValidatorContext context) {
        if (!value.isSelected()) {
            return true;
        }
        return value.getDay() != null &&
                value.getBegin() != null &&
                value.getEnd() != null &&
                value.getBegin().isBefore(value.getEnd());
    }

}
