package com.driving.planning.common.mail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class Mail {

    @NotNull
    private final MailTemplate template;

    @NotBlank
    private final String receiver;

    private final Map<String, String> properties;

    public Mail(MailTemplate template, String receiver) {
        properties = new HashMap<>();
        this.template = template;
        this.receiver = receiver;
    }

    public void addProperty(String key, String value){
        properties.put(key, value);
    }

    public MailTemplate getTemplate() {
        return template;
    }

    public String getReceiver() {
        return receiver;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
