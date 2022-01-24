package com.driving.planning.school.common.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class String2LocalTime implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        if (source.isBlank()){
            return null;
        }
        return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
