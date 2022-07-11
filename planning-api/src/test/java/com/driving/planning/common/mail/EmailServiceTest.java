package com.driving.planning.common.mail;

import com.driving.planning.common.exception.InternalErrorException;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.io.IOException;

@QuarkusTest
class EmailServiceTest {

    @Inject
    EmailService emailService;

    @Test
    void sendEmail(){
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        try(
            MockedConstruction<SendGrid> mocked = Mockito.mockConstruction(SendGrid.class,
                    ((mock, context) -> Mockito.when(mock.api(requestCaptor.capture())).thenReturn(new Response())))
        ){
            mocked.constructed();
            var receiver = "test@test.com";
            var mail = new Mail(MailTemplate.CONFIRM_INSCRIPTION, receiver);
            mail.addProperty("key_123", "value_123");
            emailService.sendEmail(mail);
            var request = requestCaptor.getValue();
            Assertions.assertThat(request.getEndpoint()).isEqualTo("mail/send");
            Assertions.assertThat(request.getBody())
                    .containsIgnoringCase(receiver)
                    .containsIgnoringCase("key_123")
                    .containsIgnoringCase("value_123");
        }
    }

    @Test
    void sendOnError(){
        try(
                MockedConstruction<SendGrid> mocked = Mockito.mockConstruction(SendGrid.class,
                        ((mock, context) -> Mockito.when(mock.api(Mockito.any())).thenThrow(new IOException())))
        ){
            mocked.constructed();
            var receiver = "test@test.com";
            var mail = new Mail(MailTemplate.CONFIRM_INSCRIPTION, receiver);
            Assertions.assertThatThrownBy(() -> emailService.sendEmail(mail))
                .isInstanceOf(InternalErrorException.class);
        }
    }

}
