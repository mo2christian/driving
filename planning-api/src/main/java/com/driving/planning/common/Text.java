package com.driving.planning.common;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
public class Text implements Serializable {

    private String message;

    private List<String> details;

    public Text() {
        details = new LinkedList<>();
    }

    public List<String> getDetails() {
        return details;
    }

    public void addDetail(String detail){
        details.add(detail);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
