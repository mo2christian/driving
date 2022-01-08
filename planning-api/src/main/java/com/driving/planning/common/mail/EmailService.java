package com.driving.planning.common.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;

@ApplicationScoped
public class EmailService {

    @ConfigProperty(name = "app.email.key")
    String emailKey;

    @ConfigProperty(name = "app.email.sender")
    String sender;

    @Inject
    Logger logger;

    public void sendEmail(@Valid com.driving.planning.common.mail.Mail message){
        logger.debugf("Send mail to %s", message.getReceiver());
        var from = new Email(sender);
        var mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(message.getTemplate().getId());

        var personalization = new Personalization();
        var to = new Email(message.getReceiver());
        personalization.addTo(to);
        message.getProperties().forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);

        var sg = new SendGrid(emailKey);
        var request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            logger.errorf(ex,"Unable to send mail of type %s", message.getTemplate());
            throw new EmailException("Unable to send mail", ex);
        }
    }

}
