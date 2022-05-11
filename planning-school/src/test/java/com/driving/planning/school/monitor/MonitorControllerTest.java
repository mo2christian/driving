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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

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
        var workDays = new HashSet<Hourly>();
        for (var day : Day.values()){
            var hourly = new Hourly();
            hourly.setDay(day);
            hourly.setBegin("09:00");
            hourly.setEnd("17:00");
            workDays.add(hourly);
        }

        var monitor = new MonitorDto()
                .id("id")
                .firstName("test")
                .lastName("test")
                .phoneNumber("147852369")
                .workDays(workDays.stream()
                        .filter(wd -> wd.getDay() != Day.FR)
                        .collect(Collectors.toSet()));
        response = new MonitorResponse()
                .addMonitorsItem(monitor);
        when(monitorApiClient.getMonitors(TENANT))
                .thenReturn(ResponseEntity.ok(response));

        when(monitorApiClient.getMonitorById("id", TENANT))
                .thenReturn(ResponseEntity.ok(monitor));

        var school = new SchoolDto()
                .name("school")
                .pseudo(TENANT)
                .workDays(workDays);
        when(schoolApiClient.getSchoolByID(TENANT))
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
        verify(monitorApiClient, Mockito.atMostOnce()).getMonitors(TENANT);
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
        verify(monitorApiClient, atMostOnce()).addMonitor(eq(TENANT), monitorCaptor.capture());
        assertThat(monitorCaptor.getValue())
                .isNotNull()
                .extracting(MonitorDto::getLastName, MonitorDto::getFirstName, MonitorDto::getPhoneNumber)
                .containsExactly(dto.getLastName(), dto.getFirstName(), dto.getPhoneNumber());
        assertThat(monitorCaptor.getValue().getWorkDays())
                .isNotNull()
                .hasSize(6)
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
        verify(monitorApiClient, atMostOnce()).addMonitor(eq(TENANT), monitorCaptor.capture());
        assertThat(monitorCaptor.getValue())
                .isNotNull()
                .extracting(MonitorDto::getLastName, MonitorDto::getFirstName, MonitorDto::getPhoneNumber)
                .containsExactly(dto.getLastName(), dto.getFirstName(), dto.getPhoneNumber());
        assertThat(monitorCaptor.getValue().getWorkDays())
                .isNotNull()
                .hasSize(4)
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
        verify(monitorApiClient, never()).addMonitor(eq(TENANT), monitorCaptor.capture());
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void getMonitor() throws Exception {
        var monitor = response.getMonitors().get(0);
        final var id = "id";
        mockMvc.perform(get("/monitor?id={id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attributeExists("request", "monitors"))
                .andExpect(model().attribute("monitors", hasSize(1)))
                .andExpect(model().attribute("monitors", hasItem(
                        allOf(
                                hasProperty("firstName", is(monitor.getFirstName())),
                                hasProperty("lastName", is(monitor.getLastName())),
                                hasProperty("phoneNumber", is(monitor.getPhoneNumber())),
                                hasProperty("workDays", hasSize(monitor.getWorkDays().size()))
                        )
                )))
                .andExpect(model().attribute("request", allOf(
                                hasProperty("firstName", is(monitor.getFirstName())),
                                hasProperty("lastName", is(monitor.getLastName())),
                                hasProperty("phoneNumber", is(monitor.getPhoneNumber())),
                                hasProperty("workDays", hasSize(7)) //nombre de jour de la semaine
                        )
                ));
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
