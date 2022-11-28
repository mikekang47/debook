package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final Long EXISTS_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {
        @Nested
        @DisplayName("올바른 요청이 들어왔을 때")
        class Context_when_gives_valid_requests {
            @BeforeEach
            void setUp() {
                given(userService.createUser(any(UserRequestData.class))).will(invocation -> {
                    UserRequestData source = invocation.getArgument(0);
                    return User.builder()
                            .id(EXISTS_ID)
                            .email(source.getEmail())
                            .password(source.getPassword())
                            .githubId(source.getGithubId())
                            .nickName(source.getNickName())
                            .build();
                });
            }

            @Test
            @DisplayName("201 응답과 생성된 사용자를 리턴한다.")
            void It_responds_201_and_user() throws Exception {
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test@email.com\"," +
                                        "\"nickName\":\"Tester\"," +
                                        "\"password\":\"12345678901234\", " +
                                        "\"githubId\":\"test\"}")
                        )
                        .andExpect(content().string(containsString("test@email.com")))
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 요청이 들어왔을 때")
        class Context_when_gives_invalid_requests {
            @Test
            @DisplayName("400을 응답한다.")
            void It_responds_400() throws Exception {
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test\"," +
                                        "\"nickName\":\"Tester\", " +
                                        "\"gitNickName\":\"test\"}")
                        )
                        .andExpect(status().isBadRequest());

            }
        }
    }

}
