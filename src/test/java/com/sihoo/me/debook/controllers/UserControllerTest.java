package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.errors.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final Long EXISTS_ID = 1L;
    private static final Long NOT_EXISTS_ID = 200L;
    private static final String EXISTS_NICK_NAME = "exists";
    private static final String NOT_EXISTS_NICK_NAME = "notexists";

    private static User user;

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

    @Nested
    @DisplayName("detailById 메서드는")
    class Describe_detail {

        @Nested
        @DisplayName("유저가 존재할 때")
        class Context_when_exists_id {

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .id(EXISTS_ID)
                        .email("test@email.com")
                        .build();

                given(userService.getUserById(EXISTS_ID)).willReturn(user);
            }

            @Test
            @DisplayName("200과 생성된 유저를 반환한다.")
            void It_responds_200_and_user() throws Exception {
                mockMvc.perform(get("/users/" + EXISTS_ID)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().string(containsString("test@email.com")))
                        .andExpect(status().isOk());

                verify(userService).getUserById(EXISTS_ID);
            }
        }

        @Nested
        @DisplayName("유저가 존재하지 않을 때")
        class Context_when_not_exists_id {
            @BeforeEach
            void setUp() {
                given(userService.getUserById(NOT_EXISTS_ID)).willThrow(new CustomException("[ERROR] User not found(Id: "
                        + NOT_EXISTS_ID + ")", HttpStatus.NOT_FOUND));
            }

            @Test
            @DisplayName("404에러를 응답한다.")
            void It_responds_404() throws Exception {
                mockMvc.perform(get("/users/" + NOT_EXISTS_ID)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().string(containsString("User not found")))
                        .andExpect(status().isNotFound());

                verify(userService).getUserById(NOT_EXISTS_ID);

            }
        }
    }

    @Nested
    @DisplayName("detailByName 메서드는")
    class Describe_detailByName {
        @Nested
        @DisplayName("요청 닉네임을 포함하는 유저가 있을 때")
        class Context_when_exists_contains_user_nick_name {
            @BeforeEach
            void setUp() {
                User user1 = User.builder()
                        .nickName("exists_1")
                        .build();

                User user2 = User.builder()
                        .nickName("exists_2")
                        .build();

                List<User> users = List.of(user1, user2);

                given(userService.getUserByNickName(EXISTS_NICK_NAME))
                        .willReturn(users);
            }

            @Test
            @DisplayName("200과 포함된 유저 전체를 반환한다.")
            void It_responds_200_and_list_of_users() throws Exception {
                mockMvc.perform(get("/users/search/" + EXISTS_NICK_NAME)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().string(containsString("exists_1")))
                        .andExpect(content().string(containsString("exists_2")))
                        .andExpect(status().isOk());

            }
        }

        @Nested
        @DisplayName("요청 닉네임을 포함하는 유저가 없을 때")
        class Context_when_not_exists_contains_user_nick_name {
            @BeforeEach
            void setUp() {
                given(userService.getUserByNickName(NOT_EXISTS_NICK_NAME)).willReturn(List.of());
            }

            @Test
            @DisplayName("200 과 빈 리스트를 반환한다.")
            void It_responds_200_and_empty_list() throws Exception {
                mockMvc.perform(get("/users/search/" + NOT_EXISTS_NICK_NAME)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk());
            }
        }
    }

}
