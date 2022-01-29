package com.driving.planning.school.monitor;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.MonitorApiClient;
import com.driving.planning.client.model.*;
import com.driving.planning.school.auth.MockUser;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MonitorControllerTest {

    private static final String TENANT = "test";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MonitorApiClient monitorApiClient;

    @MockBean
    DrivingSchoolApiClient schoolApiClient;

    @Autowired
    Validator validator;

    MonitorResponse response;

    @BeforeEach
    void before(){
        var monitor = new MonitorDto()
                .firstName("test")
                .lastName("test")
                .phoneNumber("147852369");
        response = new MonitorResponse()
                .addMonitorsItem(monitor);
        when(monitorApiClient.apiV1MonitorsGet(TENANT))
                .thenReturn(ResponseEntity.ok(response));

        var workDays = new HashSet<Hourly>();
        for (var day : Day.values()){
            var hourly = new Hourly();
            hourly.setDay(day);
            hourly.setBegin("09:00");
            hourly.setEnd("17:00");
            workDays.add(hourly);
        }
        var school = new SchoolDto()
                .name("school")
                .pseudo(TENANT)
                .workDays(workDays);
        when(schoolApiClient.apiV1SchoolsIdGet(TENANT))
                .thenReturn(ResponseEntity.ok(school));
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void list() throws Exception {
        mockMvc.perform(get("/monitor"))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
        verify(monitorApiClient, Mockito.atMostOnce()).apiV1MonitorsGet(TENANT);
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void add() throws Exception {
        var dto = new MonitorDto()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("147852369");
        mockMvc.perform(post("/monitor")
                .param("firstName", dto.getFirstName())
                .param("lastName", dto.getLastName())
                .param("phoneNumber", dto.getPhoneNumber())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
        ArgumentCaptor<MonitorDto> monitorCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorApiClient, atMostOnce()).apiV1MonitorsPost(eq(TENANT), monitorCaptor.capture());
        assertThat(monitorCaptor.getValue())
                .isNotNull()
                .extracting(MonitorDto::getLastName, MonitorDto::getFirstName, MonitorDto::getPhoneNumber)
                .containsExactly(dto.getLastName(), dto.getFirstName(), dto.getPhoneNumber());
        assertThat(monitorCaptor.getValue().getWorkDays())
                .isNotNull()
                .hasSize(7)
                .allMatch(hourly -> "09:00".equals(hourly.getBegin()) && "17:00".equals(hourly.getEnd()))
                .allMatch(hourly -> hourly.getDay() != null);
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void addWithDeselect() throws Exception {
        var dto = new MonitorDto()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("147852369");
        mockMvc.perform(post("/monitor")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("workDays[0].selected", "false")
                        .param("workDays[0].begin", "")
                        .param("workDays[1].selected", "false")
                        .param("workDays[1].end", "14:")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
        ArgumentCaptor<MonitorDto> monitorCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorApiClient, atMostOnce()).apiV1MonitorsPost(eq(TENANT), monitorCaptor.capture());
        assertThat(monitorCaptor.getValue())
                .isNotNull()
                .extracting(MonitorDto::getLastName, MonitorDto::getFirstName, MonitorDto::getPhoneNumber)
                .containsExactly(dto.getLastName(), dto.getFirstName(), dto.getPhoneNumber());
        assertThat(monitorCaptor.getValue().getWorkDays())
                .isNotNull()
                .hasSize(5)
                .allMatch(hourly -> "09:00".equals(hourly.getBegin()) && "17:00".equals(hourly.getEnd()))
                .allMatch(hourly -> hourly.getDay() != null);
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void addWithError() throws Exception {
        var dto = new MonitorDto()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("142369");
        mockMvc.perform(post("/monitor")
                        .param("firstName", dto.getFirstName())
                        .param("lastName", dto.getLastName())
                        .param("phoneNumber", dto.getPhoneNumber())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
        ArgumentCaptor<MonitorDto> monitorCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorApiClient, never()).apiV1MonitorsPost(eq(TENANT), monitorCaptor.capture());
    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "phoneNumber"})
    void validateField(String fieldName){
        var form = new MonitorForm();
        ReflectionTestUtils.setField(form, fieldName, "");
        BindingResult bindingResult = new BindException(form, "registration");
        validator.validate(form, bindingResult);
        assertThat(bindingResult.getFieldError(fieldName))
                .isNotNull();
    }

}
