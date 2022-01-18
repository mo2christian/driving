package com.driving.planning.school.login;

import com.driving.planning.client.AccountApiClient;
import com.driving.planning.client.model.AccountDto;
import com.driving.planning.client.model.Text;
import com.driving.planning.school.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {

    @MockBean
    AccountApiClient accountApiClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    void auth() throws Exception{
        var text = new Text();
        when(accountApiClient.apiV1AccountsCheckPost(anyString(), any(AccountDto.class)))
                .thenReturn(ResponseEntity.ok(text));
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "pwd")
                        .param("school", "school")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        ArgumentCaptor<String> pseudoCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccountDto> dtoCaptor = ArgumentCaptor.forClass(AccountDto.class);
        verify(accountApiClient, atLeastOnce()).apiV1AccountsCheckPost(pseudoCaptor.capture(), dtoCaptor.capture());

        assertThat(pseudoCaptor.getValue()).isEqualTo("school");
        assertThat(dtoCaptor.getValue())
                .isNotNull()
                .extracting(AccountDto::getEmail, AccountDto::getPassword)
                .containsExactly("user", "pwd");

    }

    @Test
    void authFailed() throws Exception {
        when(accountApiClient.apiV1AccountsCheckPost(anyString(), any(AccountDto.class)))
                .thenThrow(new ApiException());
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "pwd")
                        .param("school", "school")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

}
