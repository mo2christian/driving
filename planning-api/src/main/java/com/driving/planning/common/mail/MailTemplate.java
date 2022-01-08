package com.driving.planning.common.mail;

public enum MailTemplate {

    CONFIRM_INSCRIPTION("d-da59c583aefb4c05bfecf114f3e52a0b"),
    OTP("d-d1e9ce628d3a4ee98e4393a4b9edc69c");

    private final String id;

    MailTemplate(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
