package com.driving.planning.common.sms;

import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.exception.PlanningException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.Response;

@Traced
@ApplicationScoped
public class SMSService {

    private final Logger logger;

    @ConfigProperty(name = "app.sms.sid")
    String sid;

    @ConfigProperty(name = "app.sms.token")
    String token;

    @ConfigProperty(name = "app.sms.from")
    String from;

    public SMSService(Logger logger) {
        this.logger = logger;
    }

    @PostConstruct
    void init(){
        Twilio.init(sid, token);
    }

    public void sendSMS(@PhoneNumber String number, @NotBlank String content){
        logger.debugf("Send message to %s", number);
        try{
            Message.creator(
                    new com.twilio.type.PhoneNumber(number),
                    new com.twilio.type.PhoneNumber(from),
                    content)
            .create();
        }
        catch(ApiException ex){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Error while sending SMS : " + ex.getMoreInfo());
        }
    }

}
