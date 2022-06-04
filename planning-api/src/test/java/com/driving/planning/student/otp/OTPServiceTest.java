package com.driving.planning.student.otp;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.mail.EmailService;
import com.driving.planning.common.mail.Mail;
import com.driving.planning.common.mail.MailTemplate;
import com.driving.planning.common.sms.SMSService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@QuarkusTest
class OTPServiceTest {

    @InjectMock
    EmailService emailService;

    @InjectMock
    OTPRepository otpRepository;

    @InjectMock
    SMSService smsService;

    @Inject
    OTPService otpService;

    @ConfigProperty(name = "app.otp.validity")
    int validPeriod;

    @Test
    void sendByMail(){
        var student = Generator.student();
        otpService.sendOTPByMail(student);
        ArgumentCaptor<OTP> otpCaptor = ArgumentCaptor.forClass(OTP.class);
        verify(otpRepository, atMostOnce()).persist(otpCaptor.capture());
        var otp = otpCaptor.getValue();
        assertThat(otp.getStudentId()).isEqualTo(student.getId());
        assertThat(otp.getContent()).isNotEmpty();
        assertThat(otp.getCreatedDate())
                .isNotNull()
                .isBetween(LocalDateTime.now().minusMinutes(2), LocalDateTime.now());
        ArgumentCaptor<Mail> mailCaptor = ArgumentCaptor.forClass(Mail.class);
        verify(emailService, atMostOnce()).sendEmail(mailCaptor.capture());
        var mail = mailCaptor.getValue();
        assertThat(mail.getReceiver()).isEqualTo(student.getEmail());
        assertThat(mail.getTemplate()).isEqualTo(MailTemplate.OTP);
        assertThat(mail.getProperties())
                .containsKey("otp")
                .containsValue(otp.getContent());
    }

    @Test
    void sendByEmailNotFound(){
        var student = Generator.student();
        student.setEmail("");
        assertThatExceptionOfType(PlanningException.class)
                .isThrownBy(() -> otpService.sendOTPByMail(student));
    }

    @Test
    void sendBySMS(){
        var student = Generator.student();
        otpService.sendOTPBySMS(student);
        ArgumentCaptor<OTP> otpCaptor = ArgumentCaptor.forClass(OTP.class);
        verify(otpRepository, atMostOnce()).persist(otpCaptor.capture());
        var otp = otpCaptor.getValue();
        assertThat(otp.getStudentId()).isEqualTo(student.getId());
        assertThat(otp.getContent()).isNotEmpty();
        assertThat(otp.getCreatedDate())
                .isNotNull()
                .isBetween(LocalDateTime.now().minusMinutes(2), LocalDateTime.now());
        ArgumentCaptor<String> numberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService, atMostOnce()).sendSMS(numberCaptor.capture(), contentCaptor.capture());
        assertThat(numberCaptor.getValue()).isEqualTo(student.getPhoneNumber());
        assertThat(contentCaptor.getValue()).contains(otp.getContent());
    }

    @Test
    void findValidOTP(){
        final var id = "studentId";
        var otp = new OTP();
        otp.setStudentId(id);
        otp.setContent("00");
        when(otpRepository.findByStudent(id)).thenReturn(Collections.singletonList(otp));
        assertThat(otpService.findValidOTP(id))
                .isNotEmpty()
                .element(0)
                .extracting(OTP::getStudentId, OTP::getContent)
                .containsExactly(id, "00");
    }

    @Test
    void findValidOTPEmpty(){
        final var id = "studentId";
        var otp = new OTP();
        otp.setStudentId(id);
        otp.setCreatedDate(LocalDateTime.now().minusMinutes(validPeriod));
        when(otpRepository.findByStudent(id)).thenReturn(Collections.singletonList(otp));
        assertThat(otpService.findValidOTP(id))
                .isEmpty();
    }
}
