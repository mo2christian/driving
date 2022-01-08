package com.driving.planning.config.converter;

import com.driving.planning.common.DatePattern;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements ParamConverter<LocalDate> {

    private final DateTimeFormatter formatter;

    public LocalDateConverter() {
        this.formatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
    }

    @Override
    public LocalDate fromString(String value) {
        return LocalDate.parse(value, formatter);
    }

    @Override
    public String toString(LocalDate value) {
        return value.format(formatter);
    }
}
