package com.driving.planning.config.converter;

import com.driving.planning.student.otp.MethodType;

import javax.ws.rs.ext.ParamConverter;

public class MethodTypeConverter implements ParamConverter<MethodType> {
    @Override
    public MethodType fromString(String value) {
        return MethodType.parse(value);
    }

    @Override
    public String toString(MethodType value) {
        return value.getCanal();
    }
}
