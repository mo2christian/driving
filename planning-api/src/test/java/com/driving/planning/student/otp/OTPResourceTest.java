package com.driving.planning.student.otp;

import com.driving.planning.Generator;
import com.driving.planning.student.StudentService;
import com.driving.planning.student.dto.StudentDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class OTPResourceTest {

    @InjectMock
    OTPService otpService;

    @InjectMock
    StudentService studentService;

    @Test
    void sendOTPByMail(){
        var student = Generator.student();
        when(studentService.findByNumber(student.getPhoneNumber())).thenReturn(Optional.of(student));
        given()
                .header("x-app-tenant", "tenant")
                .queryParam("canal", MethodType.EMAIL.getCanal())
                .when()
                .post("/api/v1/students/{phone}/otp/send", student.getPhoneNumber())
                .then()
                .statusCode(204);
        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(otpService, atMostOnce()).sendOTPByMail(studentCaptor.capture());
        assertThat(studentCaptor.getValue())
                .extracting(StudentDto::getId, StudentDto::getEmail, StudentDto::getPhoneNumber)
                .containsExactly(student.getId(), student.getEmail(), student.getPhoneNumber());
    }

    @Test
    void sendOTPNumberNotFound(){
        when(studentService.findByNumber(anyString())).thenReturn(Optional.empty());
        given().log()
                .ifValidationFails()
                .header("x-app-tenant", "tenant")
                .queryParam("canal", MethodType.EMAIL.getCanal())
                .when()
                .post("/api/v1/students/{phone}/otp/send", "799874569")
                .then()
                .statusCode(404);
    }

    @Test
    void sendOTPEmailNotFound(){
        var student = Generator.student();
        student.setEmail(" ");
        when(studentService.findByNumber(student.getPhoneNumber())).thenReturn(Optional.of(student));
        given()
                .header("x-app-tenant", "tenant")
                .queryParam("canal", MethodType.EMAIL.getCanal())
                .when()
                .post("/api/v1/students/{phone}/otp/send", student.getPhoneNumber())
                .then()
                .statusCode(405);
    }

    @Test
    void sendOTPSMS(){
        var student = Generator.student();
        when(studentService.findByNumber(student.getPhoneNumber())).thenReturn(Optional.of(student));
        given()
                .header("x-app-tenant", "tenant")
                .queryParam("canal", MethodType.SMS.getCanal())
                .when()
                .post("/api/v1/students/{phone}/otp/send", student.getPhoneNumber())
                .then()
                .statusCode(204);
        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(otpService, atMostOnce()).sendOTPBySMS(studentCaptor.capture());
        assertThat(studentCaptor.getValue())
                .extracting(StudentDto::getId, StudentDto::getEmail, StudentDto::getPhoneNumber)
                .containsExactly(student.getId(), student.getEmail(), student.getPhoneNumber());
    }

    @Test
    void verifyOTP(){
        var student = Generator.student();
        var otp = new OTP();
        otp.setStudentId(student.getId());
        otp.setContent("00");
        when(otpService.findValidOTP(student.getId())).thenReturn(Collections.singletonList(otp));
        when(studentService.findByNumber(student.getPhoneNumber())).thenReturn(Optional.of(student));
        given()
                .header("x-app-tenant", "tenant")
                .queryParam("otp", "00")
                .when()
                .post("/api/v1/students/{phone}/otp/verify", student.getPhoneNumber())
                .then()
                .statusCode(204);
    }

    @Test
    void verifyOTPNumberNotfound(){
        when(studentService.findByNumber(anyString())).thenReturn(Optional.empty());
        given().log()
                .ifValidationFails()
                .header("x-app-tenant", "tenant")
                .queryParam("otp", "00")
                .when()
                .post("/api/v1/students/{phone}/otp/verify", "799874569")
                .then()
                .statusCode(404);
    }

    @Test
    void verifyOTPInvalid(){
        var student = Generator.student();
        var otp = new OTP();
        otp.setStudentId(student.getId());
        otp.setContent("00");
        when(otpService.findValidOTP(student.getId())).thenReturn(Collections.singletonList(otp));
        when(studentService.findByNumber(student.getPhoneNumber())).thenReturn(Optional.of(student));
        given()
                .header("x-app-tenant", "tenant")
                .queryParam("otp", "11")
                .when()
                .post("/api/v1/students/{phone}/otp/verify", student.getPhoneNumber())
                .then()
                .statusCode(400);
    }

}
