package com.driving.planning.school.student;

import com.driving.planning.client.StudentApiClient;
import com.driving.planning.client.model.StudentDto;
import com.driving.planning.client.model.StudentReservationDto;
import com.driving.planning.client.model.StudentResponse;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    private static final String TENANT = "test";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentApiClient studentApiClient;

    @BeforeEach
    void before(){
        var response = new StudentResponse();
        response.addStudentsItem(new StudentReservationDto());
        when(studentApiClient.getStudents(TENANT)).thenReturn(ResponseEntity.ok(response));
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void list() throws Exception {
        mockMvc.perform(get("/student/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("student"))
                .andExpect(model().attributeExists("studentForm", "students"));
        verify(studentApiClient, times(1)).getStudents(TENANT);
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void show() throws Exception {
        var student = new StudentReservationDto();
        student.setEmail("email");
        student.setFirstName("first");
        when(studentApiClient.getStudent("id", TENANT)).thenReturn(ResponseEntity.ok(student));
        mockMvc.perform(get("/student/show")
                        .queryParam("id", "id"))
            .andExpect(view().name("student"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("studentForm", "students"))
            .andExpect(model().attribute("studentForm", hasProperty("operation", equalTo("update"))))
            .andExpect(model().attribute("studentForm", hasProperty("firstName", equalTo("first"))))
            .andExpect(model().attribute("studentForm", hasProperty("email", equalTo("email"))));
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void addStudent() throws Exception {
        mockMvc.perform(post("/student/action")
                .param("operation", "add")
                .param("firstName", "first")
                .param("lastName", "last")
                .param("email", "mail@yahoo.fr")
                .param("phoneNumber", "789456123")
                .with(csrf()))
                .andExpect(view().name("redirect:/student/list"))
                .andExpect(status().is3xxRedirection());
        ArgumentCaptor<StudentDto> dtoCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentApiClient, times(1)).addStudent(eq(TENANT), dtoCaptor.capture());
        Assertions.assertThat(dtoCaptor.getValue())
                .isNotNull()
                .extracting(StudentDto::getEmail, StudentDto::getLastName)
                .containsExactly("mail@yahoo.fr", "last");
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void addStudentError() throws Exception {
        mockMvc.perform(post("/student/action")
                        .param("operation", "add")
                        .param("firstName", "first")
                        .param("lastName", "last")
                        .param("email", "mailyahoo.fr")
                        .param("phoneNumber", "789456123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "email"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/student/action")
                        .param("operation", "add")
                        .param("email", "mail@yahoo.fr")
                        .param("phoneNumber", "789456123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "firstName", "lastName"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/student/action")
                        .param("operation", "add")
                        .param("firstName", "first")
                        .param("lastName", "last")
                        .param("email", "mail@yahoo.fr")
                        .param("phoneNumber", "7896123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "phoneNumber"))
                .andExpect(status().isOk());
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void updateStudent() throws Exception {
        mockMvc.perform(post("/student/action")
                        .param("operation", "update")
                        .param("firstName", "first")
                        .param("lastName", "last")
                        .param("email", "mail@yahoo.fr")
                        .param("id", "id")
                        .param("phoneNumber", "789456123")
                        .with(csrf()))
                .andExpect(view().name("redirect:/student/list"))
                .andExpect(status().is3xxRedirection());
        ArgumentCaptor<StudentDto> dtoCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentApiClient, times(1)).updateStudent(eq("id"), eq(TENANT), dtoCaptor.capture());
        Assertions.assertThat(dtoCaptor.getValue())
                .isNotNull()
                .extracting(StudentDto::getEmail, StudentDto::getLastName)
                .containsExactly("mail@yahoo.fr", "last");
    }

    @Test
    @MockUser(username = "user", school = TENANT)
    void updateStudentError() throws Exception {
        mockMvc.perform(post("/student/action")
                        .param("operation", "update")
                        .param("firstName", "first")
                        .param("lastName", "last")
                        .param("email", "mailyahoo.fr")
                        .param("phoneNumber", "789456123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "email"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/student/action")
                        .param("operation", "update")
                        .param("email", "mail@yahoo.fr")
                        .param("phoneNumber", "789456123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "firstName", "lastName"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/student/action")
                        .param("operation", "update")
                        .param("firstName", "first")
                        .param("lastName", "last")
                        .param("email", "mail@yahoo.fr")
                        .param("phoneNumber", "7896123")
                        .with(csrf()))
                .andExpect(view().name("student"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "phoneNumber"))
                .andExpect(status().isOk());
    }

}
