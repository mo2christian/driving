package com.driving.planning.student.otp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum MethodType {

    EMAIL("email"), SMS("SMS");

    private final String canal;

    MethodType(String canal){
        this.canal = canal;
    }

    @JsonValue
    public String getCanal(){
        return canal;
    }

    @JsonCreator
    public static MethodType parse(String canal){
        return Stream.of(MethodType.values())
                .filter(method -> canal.equalsIgnoreCase(method.canal))
                .findFirst()
                .orElseThrow();
    }

}
