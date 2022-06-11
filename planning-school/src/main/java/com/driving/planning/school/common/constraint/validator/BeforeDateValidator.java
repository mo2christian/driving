package com.driving.planning.school.common.constraint.validator;

import com.driving.planning.school.common.TimeConstants;
import com.driving.planning.school.common.constraint.BeforeDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalTime;

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
            if (TimeConstants.DATE_FORMAT == constraintAnnotation.dateFormat()){
                var before = getValue(object, constraintAnnotation.beforeField(), LocalDate.class);
                var after = getValue(object, constraintAnnotation.afterField(), LocalDate.class);
                return (before.isBefore(after)) || (constraintAnnotation.allowEqual() && before.isEqual(after));
            }
            if (TimeConstants.HOUR_FORMAT == constraintAnnotation.dateFormat()){
                var before = getValue(object, constraintAnnotation.beforeField(), LocalTime.class);
                var after = getValue(object, constraintAnnotation.afterField(), LocalTime.class);
                return (before.isBefore(after)) || (constraintAnnotation.allowEqual() && before.equals(after));
            }
        }
        catch(IllegalArgumentException ex){
            log.error("Unable to validate form", ex);
        }
        return false;
    }

    @SuppressWarnings({"all"})
    private <T>T getValue(Object object, String fieldName, Class<T> cls){
        Class<?> clazz = object.getClass();
        try{
            var field = ReflectionUtils.findField(clazz, fieldName);
            if (field == null){
                throw new IllegalArgumentException(String.format("%s field not found in class %s", fieldName, clazz));
            }
            field.setAccessible(true);
            var value = field.get(object);
            if (!cls.isInstance(value)){
                throw new IllegalArgumentException(String.format("%s field in class %s is not of type %s", fieldName, clazz, cls));
            }
            return (T) value ;
        }
        catch(IllegalAccessException ex){
            throw new IllegalArgumentException(ex);
        }
    }
}
