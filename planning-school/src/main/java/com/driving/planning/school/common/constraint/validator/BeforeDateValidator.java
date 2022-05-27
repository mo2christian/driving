package com.driving.planning.school.common.constraint.validator;

import com.driving.planning.school.common.constraint.BeforeDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Slf4j
public class BeforeDateValidator implements ConstraintValidator<BeforeDate, Object> {

    private BeforeDate constraintAnnotation;

    @Override
    public void initialize(BeforeDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try{
            var before = getValue(object, constraintAnnotation.beforeField());
            var after = getValue(object, constraintAnnotation.afterField());
            return (before.isBefore(after)) || (constraintAnnotation.allowEqual() && before.isEqual(after));
        }
        catch(IllegalArgumentException ex){
            log.error("Unable to validate Absent form", ex);
        }
        return false;
    }

    private LocalDate getValue(Object object, String fieldName){
        Class<?> clazz = object.getClass();
        try{
            var field = ReflectionUtils.findField(clazz, fieldName);
            if (field == null){
                throw new IllegalArgumentException(String.format("%s field not found in class %s", fieldName, clazz));
            }
            field.setAccessible(true);
            var value = field.get(object);
            if (!(value instanceof LocalDate)){
                throw new IllegalArgumentException(String.format("%s field in class %s is not of type LocalDate", fieldName, clazz));
            }
            return (LocalDate) value ;
        }
        catch(IllegalAccessException ex){
            throw new IllegalArgumentException(ex);
        }
    }
}
