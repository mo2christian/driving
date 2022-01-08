package com.driving.planning.config.converter;

import com.driving.planning.student.otp.MethodType;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

@Provider
public class ConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (LocalDate.class.isAssignableFrom(rawType)){
            return (ParamConverter<T>) new LocalDateConverter();
        }
        if (MethodType.class.isAssignableFrom(rawType)){
            return (ParamConverter<T>) new MethodTypeConverter();
        }
        return null;
    }
}
