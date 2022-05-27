package com.driving.planning.school.common.converter;

import com.driving.planning.school.common.TimeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class String2LocalDate implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        if (source.isBlank()){
            return null;
        }
        try{
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(TimeConstants.DATE_FORMAT.value()));
        }
        catch(DateTimeParseException ex){
            log.warn("Error while parsing hour");
            return null;
        }
    }
}
