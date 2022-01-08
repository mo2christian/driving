package com.driving.planning.common.sms;

import com.driving.planning.common.exception.PlanningException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@QuarkusTest
class SMSSeviceTest {

    @Inject
    SMSService smsService;

    @ConfigProperty(name = "app.sms.from")
    String fromNumber;

    @Test
    void sendSMS(){
        try(var mocked = mockStatic(Message.class)){
            ArgumentCaptor<PhoneNumber> toCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
            ArgumentCaptor<PhoneNumber> fromCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
            var messageCreator = mock(MessageCreator.class);
            mocked.when(() -> Message.creator(toCaptor.capture(), fromCaptor.capture(), contentCaptor.capture()))
                    .thenReturn(messageCreator);
            smsService.sendSMS("014587456", "test");
            assertThat(toCaptor.getValue().getEndpoint()).isEqualTo("014587456");
            assertThat(fromCaptor.getValue().getEndpoint()).isEqualTo(fromNumber);
            assertThat(contentCaptor.getValue()).isEqualTo("test");
        }
    }

    @Test
    void sendSMSError(){
        try(var mocked = mockStatic(Message.class)){
            var messageCreator = mock(MessageCreator.class);
            mocked.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString())).thenThrow(ApiException.class);
            assertThatExceptionOfType(PlanningException.class)
                    .isThrownBy(() -> smsService.sendSMS("014587456", "test"));
        }
    }

}
