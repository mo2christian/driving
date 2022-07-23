package com.driving.planning.school.agency;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.Day;
import com.driving.planning.client.model.Hourly;
import com.driving.planning.client.model.SchoolDto;
import com.driving.planning.school.auth.MockUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AgencyControllerTest {

    private static final String TENANT = "test";

    @MockBean
    private DrivingSchoolApiClient schoolApiClient;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init(){
        when(schoolApiClient.getSchoolByID(anyString())).thenReturn(ResponseEntity.ok(new SchoolDto()));
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void list() throws Exception {
        mockMvc.perform(get("/school/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("school"))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void update() throws Exception {
        mockMvc.perform(post("/school/action")
                .with(csrf())
                .param("id", "id")
                .param("operation", "update")
                .param("name", "name")
                .param("phoneNumber", "1234567890")
                .param("path", "path")
                .param("zipCode", "12345")
                .param("town", "town")
                .param("workDays[0].begin", "08:00")
                .param("workDays[0].end", "18:00")
                .param("workDays[0].day", "MO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/school/list"));
        ArgumentCaptor<SchoolDto> schoolCaptor = ArgumentCaptor.forClass(SchoolDto.class);
        verify(schoolApiClient, times(1)).updateSchool(eq("id"), schoolCaptor.capture());
        Assertions.assertThat(schoolCaptor.getValue())
                .isNotNull()
                .extracting(SchoolDto::getName, SchoolDto::getPhoneNumber)
                .containsExactly("name", "1234567890");
        Assertions.assertThat(schoolCaptor.getValue().getWorkDays())
                .hasSize(1)
                .element(0)
                .extracting(Hourly::getDay, Hourly::getBegin, Hourly::getEnd)
                .containsExactly(Day.MO, "08:00:00", "18:00:00");
    }

}
