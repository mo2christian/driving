package com.driving.planning.school.register;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.*;
import com.driving.planning.school.common.form.WorkDayForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegisterControllerTest {

    @MockBean
    DrivingSchoolApiClient schoolApiClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Validator validator;

    RegistrationForm registration;

    @BeforeEach
    void before(){
        registration = new RegistrationForm();
        registration.setEmail("test@test.com");
        registration.setName("name");
        registration.setPassword("pwd");
        registration.setPath("10 rue du success");
        registration.setTown("Londres");
        registration.setPhoneNumber("147852369");
        registration.setZipCode("25874");
        for (var day : Day.values()){
            var wd = new WorkDayForm();
            wd.setSelected(day != Day.SU);
            wd.setBegin(LocalTime.of(8,0));
            wd.setEnd(LocalTime.of(17,0));
            wd.setDay(day);
            registration.getWorkDays().add(wd);
        }
    }

    @Test
    void postRegister() throws Exception {
        var formatter = DateTimeFormatter.ofPattern("HH:mm");
        mockMvc.perform(post("/register")
                        .param("email", registration.getEmail())
                        .param("name", registration.getName())
                        .param("password", registration.getPassword())
                        .param("path", registration.getPath())
                        .param("town", registration.getTown())
                        .param("phoneNumber", registration.getPhoneNumber())
                        .param("zipCode", registration.getZipCode())
                        .param("workDays[0].begin", formatter.format(registration.getWorkDays().get(0).getBegin()))
                        .param("workDays[0].end", formatter.format(registration.getWorkDays().get(0).getEnd()))
                        .param("workDays[0].day", registration.getWorkDays().get(0).getDay().toString())
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        ArgumentCaptor<SchoolRequest> request = ArgumentCaptor.forClass(SchoolRequest.class);
        Mockito.verify(schoolApiClient, Mockito.atLeastOnce()).addSchool(request.capture());
        assertThat(request.getValue())
                .isNotNull();
        assertThat(request.getValue().getAccount())
                .isNotNull()
                .extracting(AccountDto::getEmail, AccountDto::getPassword)
                .containsExactly(registration.getEmail(), registration.getPassword());

        assertThat(request.getValue().getSchool())
                .isNotNull()
                .extracting(SchoolDto::getName, SchoolDto::getPhoneNumber)
                .containsExactly(registration.getName(), registration.getPhoneNumber());

        assertThat(request.getValue().getSchool().getAddress())
                .isNotNull()
                .extracting(AddressDto::getPath, AddressDto::getTown, AddressDto::getPostalCode)
                .containsExactly(registration.getPath(), registration.getTown(), registration.getZipCode());
    }

    @Test
    void getRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(view().name("subscription"));
    }

    @Test
    void postRegisterWithError() throws Exception {
        registration.setEmail("toto");
        mockMvc.perform(post("/register")
                        .param("email", registration.getEmail())
                        .param("name", registration.getName())
                        .param("password", registration.getPassword())
                        .param("path", registration.getPath())
                        .param("town", registration.getTown())
                        .param("phoneNumber", registration.getPhoneNumber())
                        .param("zipCode", registration.getZipCode())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("subscription"));
        Mockito.verify(schoolApiClient, Mockito.never()).addSchool(Mockito.any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "name", "password", "path", "town", "phoneNumber", "zipCode"})
    void validateField(String fieldName){
        ReflectionTestUtils.setField(registration, fieldName, "");
        BindingResult bindingResult = new BindException(registration, "registration");
        validator.validate(registration, bindingResult);
        assertThat(bindingResult.getFieldError(fieldName))
                .isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"toto@", "toto@@toto.com"})
    void validateEmail(String value){
        registration.setEmail(value);
        BindingResult bindingResult = new BindException(registration, "registration");
        validator.validate(registration, bindingResult);
        assertThat(bindingResult.getFieldError("email"))
                .isNotNull();
    }

}
