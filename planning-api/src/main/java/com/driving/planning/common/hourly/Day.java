package com.driving.planning.common.hourly;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.DayOfWeek;
import java.util.stream.Stream;

@Schema(enumeration = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"})
public enum Day {
    MONDAY(DayOfWeek.MONDAY, "MO"),
    TUESDAY(DayOfWeek.TUESDAY, "TU"),
    WEDNESDAY(DayOfWeek.WEDNESDAY, "WE"),
    THURSDAY(DayOfWeek.THURSDAY, "TH"),
    FRIDAY(DayOfWeek.FRIDAY, "FR"),
    SATURDAY(DayOfWeek.SATURDAY, "SA"),
    SUNDAY(DayOfWeek.SUNDAY, "SU");

    private final String value;

    private final DayOfWeek dayOfWeek;

    Day(DayOfWeek dayOfWeek, String value){
        this.value = value;
        this.dayOfWeek = dayOfWeek;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @JsonCreator
    public static Day parse(String value){
        return Stream.of(Day.values())
                .filter(d -> d.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unkown day " + value));
    }

}
