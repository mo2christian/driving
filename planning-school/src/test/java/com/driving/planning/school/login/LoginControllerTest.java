package com.driving.planning.school.login;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.SchoolDto;
import com.driving.planning.client.model.SchoolResponse;
import com.driving.planning.school.common.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @MockBean
    DrivingSchoolApiClient schoolApiClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    void login() throws Exception {
        var dto = new SchoolDto()
                .pseudo("pseudo")
                .name("name");
        var school = new SchoolResponse();
        school.setSchools(List.of(dto));
        when(schoolApiClient.apiV1SchoolsGet()).thenReturn(ResponseEntity.ok(school));
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("schools", school.getSchools()));
    }

    @Test
    void loginWithError() throws Exception {
        when(schoolApiClient.apiV1SchoolsGet()).thenThrow(ApiException.class);
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("schools"));
    }

}
