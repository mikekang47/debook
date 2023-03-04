package com.sihoo.me.debook.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.errors.PasswordNotMatchException;
import com.sihoo.me.debook.errors.UserNotFoundException;

@WebMvcTest(SessionController.class)
class SessionControllerTest {
    private static final String EXISTS_EMAIL = "exists@email.com";
    private static final String NOT_EXISTS_EMAIL = "notexists@email.com";
    private static final String VALID_PASSWORD = "123456789123";
    private static final String WRONG_PASSWORD = "wrongwrongwrong";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {
        @Nested
        @DisplayName("올바른 요청이 주어졌을 때")
        class Context_when_gives_valid_requests {
            @BeforeEach
            void setUp() {
                given(authenticationService.login(EXISTS_EMAIL, VALID_PASSWORD)).willReturn("accessToken");
            }

            @Test
            @DisplayName("200과 액세스 토큰을 반환한다.")
            void It_responds_200_and_returns_access_token() throws Exception {
                mvc.perform(post("/session")
                                .content("{\"email\":\"exists@email.com\", \"password\" : \"123456789123\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 이메일이 주어졌을 때")
        class Context_when_gives_invalid_email {
            @Test
            @DisplayName("400에러를 발생한다.")
            void It_responds_400() throws Exception {
                mvc.perform(post("/session")
                                .content("{\"email\":\"exists\", \"password\" : \"123456789123\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().string(containsString("[ERROR]")))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 이메일이 주어졌을 때")
        class Context_when_gives_not_exists_email_and_right_password {
            @BeforeEach
            void setUp() {
                given(authenticationService.login(NOT_EXISTS_EMAIL, VALID_PASSWORD)).willThrow(
                        new UserNotFoundException(NOT_EXISTS_EMAIL)
                );
            }

            @Test
            @DisplayName("404에러를 반환한다.")
            void It_responds_400() throws Exception {
                mvc.perform(post("/session")
                                .content("{\"email\":\"notexists@email.com\", \"password\" : \"123456789123\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("틀린 비밀번호가 주어졌을 때")
        class Context_when_gives_exists_email_and_wrong_password {
            @BeforeEach
            void setUp() {
                given(authenticationService.login(EXISTS_EMAIL, WRONG_PASSWORD)).willThrow(
                        new PasswordNotMatchException(EXISTS_EMAIL)
                );
            }

            @Test
            @DisplayName("400에러를 반환한다.")
            void It_responds_400() throws Exception {
                mvc.perform(post("/session")
                                .content("{\"email\":\"exists@email.com\", \"password\" : \"wrongwrongwrong\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isBadRequest());
            }
        }
    }
}
