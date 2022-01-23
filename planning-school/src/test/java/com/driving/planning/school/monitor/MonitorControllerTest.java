package com.driving.planning.school.monitor;

import com.driving.planning.client.MonitorApiClient;
import com.driving.planning.client.model.MonitorDto;
import com.driving.planning.client.model.MonitorResponse;
import com.driving.planning.school.auth.MockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MonitorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MonitorApiClient monitorApiClient;

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
        Mockito.when(monitorApiClient.apiV1MonitorsGet("test"))
                .thenReturn(ResponseEntity.ok(response));
    }

    @Test
    @MockUser(username = "user", school = "test")
    void list() throws Exception {

        mockMvc.perform(get("/monitor"))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    @MockUser(username = "user", school = "test")
    void add() throws Exception {
        mockMvc.perform(post("/monitor")
                .param("firstName", "firstName")
                .param("lastName", "lastName")
                .param("phoneNumber", "147852369")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    @MockUser(username = "user", school = "test")
    void addWithError() throws Exception {
        mockMvc.perform(post("/monitor")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("phoneNumber", "147852")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("monitors", response.getMonitors()))
                .andExpect(model().attributeExists("request"));
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
