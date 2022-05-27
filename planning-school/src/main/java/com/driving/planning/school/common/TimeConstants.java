package com.driving.planning.school.common;

public enum TimeConstants {

    HOUR_FORMAT("HH:mm"),

    DATE_FORMAT("yyyy-MM-dd");

    private final String value;

    TimeConstants(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

}
