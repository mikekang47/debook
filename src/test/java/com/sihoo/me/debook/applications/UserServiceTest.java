package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.dto.UserUpdateRequest;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private UserService userService;

    @MockBean
    private RoleService roleService;

    private static UserUpdateRequest userUpdateRequest;

    private static final Long NEW_ID = 5L;
    private static final Long EXISTS_ID = 2L;
    private static final Long SAME_CURRENT_USER_ID = 2L;
    private static final Long DIFF_CURRENT_USER_ID = 8L;
    private static final Long NOT_EXISTS_ID = 200L;
    private static final String EXISTS_NICKNAME = "exists";
    private static final String NOT_EXISTS_NICKNAME = "not_exists";

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(roleService, userRepository, mapper, passwordEncoder);
    }

    @Nested
    @DisplayName("createUser ????????????")
    class Describe_createUser {
        @Nested
        @DisplayName("????????? ????????? ???????????? ???")
        class Context_when_valid_requests {
            @BeforeEach
            void setUp() {
                given(userRepository.save(any(User.class))).will(invocation -> {
                    User source = invocation.getArgument(0);
                    return User.builder()
                            .id(NEW_ID)
                            .email(source.getEmail())
                            .password(source.getPassword())
                            .githubId(source.getGithubId())
                            .nickName(source.getNickName())
                            .build();
                });
            }

            @Test
            @DisplayName("????????? ????????????.")
            void It_creates_user_and_returns_user() {
                UserRequestData userRequest = UserRequestData.builder()
                        .nickName("test")
                        .email("test@email.com")
                        .password("password123")
                        .githubId("mikekang47")
                        .build();

                User user = userService.createUser(userRequest);

                assertThat(user.getEmail()).isEqualTo("test@email.com");
                assertThat(user.getNickName()).isEqualTo("test");
                assertThat(user.getGithubId()).isEqualTo("mikekang47");
            }
        }
    }

    @Nested
    @DisplayName("getUserById ????????????")
    class Describe_getUserById {
        @Nested
        @DisplayName("userId??? ????????? ???")
        class Context_when_exists_user {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .email("test@email.com")
                        .build();

                given(userRepository.findUserById(EXISTS_ID)).willReturn(Optional.of(user));
            }

            @Test
            @DisplayName("user??? ????????????.")
            void It_returns_user() {
                User user = userService.getUserById(EXISTS_ID);

                assertThat(user.getId()).isEqualTo(2L);
                assertThat(user.getEmail()).isEqualTo("test@email.com");
            }

        }

        @Nested
        @DisplayName("user??? ???????????? ?????? ???")
        class Context_when_not_exists_user {
            @Test
            @DisplayName("????????? ????????????.")
            void It_throws_not_found_exception() {
                assertThatThrownBy(() -> userService.getUserById(NOT_EXISTS_ID))
                        .hasMessageContaining("[ERROR] User not found")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("getUserByNickName ????????????")
    class Describe_getUserBtyNickname {
        @Nested
        @DisplayName("????????? ???????????? ????????? ????????? ????????????")
        class Context_exists_contains_nickname_user {
            @BeforeEach
            void setUp() {
                User user1 = User.builder()
                        .nickName("exists_1")
                        .build();

                User user2 = User.builder()
                        .nickName("exists_2")
                        .build();

                List<User> users = List.of(user1, user2);

                given(userRepository.findUserByNickName(EXISTS_NICKNAME))
                        .willReturn(users);
            }

            @Test
            @DisplayName("????????? ????????? ?????? ????????????.")
            void It_returns_list_of_users() {
                List<User> users = userService.getUserByNickName(EXISTS_NICKNAME);

                assertThat(users).isNotEmpty();
            }
        }

        @Nested
        @DisplayName("????????? ???????????? ????????? ????????? ???????????? ????????????")
        class Context_not_exists_contains_nickname_user {
            @BeforeEach
            void setUp() {
                given(userRepository.findUserByNickName(NOT_EXISTS_NICKNAME)).willReturn(List.of());
            }

            @Test
            @DisplayName("??? ???????????? ????????????.")
            void It_returns_empty_list() {
                List<User> users = userService.getUserByNickName(NOT_EXISTS_NICKNAME);

                assertThat(users).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("getUsers ????????????")
    class Describe_getUsers {
        @Nested
        @DisplayName("????????? ????????????")
        class Context_exists_user {
            @BeforeEach
            void setUp() {
                User user1 = User.builder()
                        .nickName("exists_1")
                        .isDeleted(false)
                        .build();

                User user2 = User.builder()
                        .nickName("exists_2")
                        .isDeleted(false)
                        .build();

                List<User> users = List.of(user1, user2);

                given(userRepository.findUsers())
                        .willReturn(users);
            }

            @Test
            @DisplayName("????????? ?????? ????????????.")
            void It_returns_list_of_users() {
                List<User> users = userService.getUsers();

                assertThat(users).isNotEmpty();
            }
        }

        @Nested
        @DisplayName("????????? ???????????? ????????????")
        class Context_not_exists_contains_nickname_user {
            @BeforeEach
            void setUp() {
                given(userRepository.findUsers()).willReturn(List.of());
            }

            @Test
            @DisplayName("??? ???????????? ????????????.")
            void It_returns_empty_list() {
                List<User> users = userService.getUsers();

                assertThat(users).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("updateUser ????????????")
    class Describe_updateUser {
        @Nested
        @DisplayName("????????? ???????????????")
        class Context_exists_user {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .email("test@email.com")
                        .nickName("original_nickname")
                        .password("original_password")
                        .githubId("original_githubId")
                        .build();

                given(userRepository.findUserById(EXISTS_ID)).willReturn(Optional.of(user));
            }


            @Nested
            @DisplayName("????????? ????????? ???????????? ???")
            class Context_when_valid_requests {
                @Test
                @DisplayName("?????? ????????? ?????? ?????? ????????? ????????????.")
                void It_returns_updated_user() {
                    userUpdateRequest = UserUpdateRequest.builder()
                            .nickName("nickname")
                            .githubId("githubId")
                            .password("password")
                            .build();

                    User user = userService.updateUser(EXISTS_ID, userUpdateRequest, SAME_CURRENT_USER_ID);

                    assertThat(user.getNickName()).isEqualTo("nickname");
                    assertThat(user.getGithubId()).isEqualTo("githubId");
                    assertThat(user.getPassword()).isNotEqualTo("original_password");
                }
            }
        }

        @Nested
        @DisplayName("????????? ???????????? ????????????")
        class Describe_not_exists_user {
            @BeforeEach
            void setUp() {
                given(userRepository.findUserById(NOT_EXISTS_ID)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("????????? ???????????????.")
            void It_returns_empty_list() {
                assertThatThrownBy(() -> userService.updateUser(NOT_EXISTS_ID, userUpdateRequest, NOT_EXISTS_ID))
                        .hasMessageContaining("[ERROR] User not found")
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("??????????????? ???????????? ?????? ???????????? ????????????")
        class Describe_different_user {
            @Test
            @DisplayName("????????? ???????????????.")
            void It_returns_empty_list() {
                assertThatThrownBy(() -> userService.updateUser(NOT_EXISTS_ID, userUpdateRequest, DIFF_CURRENT_USER_ID))
                        .hasMessageContaining("[ERROR] No authorization for user")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteUser ????????????")
    class Describe_deleteUser {
        @Nested
        @DisplayName("????????? ????????? ???")
        class Context_when_exists_user {

            @Nested
            @DisplayName("?????? ???????????? ?????? ???")
            class Context_when_same_current_user {
                @BeforeEach
                void setUp() {
                    User user = User.builder()
                            .id(EXISTS_ID)
                            .build();

                    given(userRepository.findUserById(EXISTS_ID)).willReturn(Optional.of(user));
                }

                @Test
                @DisplayName("user??? isDeleted????????? ????????????, user??? ?????????.")
                void It_returns_user() {
                    User user = userService.deleteUser(EXISTS_ID, SAME_CURRENT_USER_ID);

                    assertThat(user.isDeleted()).isTrue();
                }
            }

            @Nested
            @DisplayName("?????? ???????????? ?????? ???")
            class Context_when_different_current_user {
                @Test
                @DisplayName("????????? ????????????.")
                void It_returns_user() {
                    assertThatThrownBy(() -> userService.deleteUser(EXISTS_ID, DIFF_CURRENT_USER_ID))
                            .hasMessageContaining("[ERROR] No authorization for user")
                            .isInstanceOf(CustomException.class);

                }
            }
        }

        @Nested
        @DisplayName("userId??? ???????????? ?????? ???")
        class Context_when_gives_not_exists_id {

            @Nested
            @DisplayName("?????? ???????????? ?????? ???")
            class Context_when_same_current_user {
                @BeforeEach
                void setUp() {
                    given(userRepository.findUserById(NOT_EXISTS_ID))
                            .willReturn(Optional.empty());
                }

                @Test
                @DisplayName("????????? ????????????.")
                void It_throws_user_not_found_exception() {
                    assertThatThrownBy(() -> userService.deleteUser(NOT_EXISTS_ID, NOT_EXISTS_ID))
                            .hasMessageContaining("[ERROR] User not found(Id: ")
                            .isInstanceOf(CustomException.class);
                }
            }

            @Nested
            @DisplayName("?????? ???????????? ?????? ???")
            class Context_when_different_current_user {
                @Test
                @DisplayName("????????? ????????????.")
                void It_returns_user() {
                    assertThatThrownBy(() -> userService.deleteUser(EXISTS_ID, DIFF_CURRENT_USER_ID))
                            .hasMessageContaining("[ERROR] No authorization for user")
                            .isInstanceOf(CustomException.class);

                }
            }
        }
    }

    @Nested
    @DisplayName("increaseReviewCount ????????????")
    class Describe_increaseReviewCount {
        @Nested
        @DisplayName("????????? ????????? ???")
        class Context_when_exists_user {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .reviewCount(2)
                        .build();

                given(userRepository.findUserById(EXISTS_ID)).willReturn(Optional.of(user));
            }

            @Test
            @DisplayName("???????????? ?????? ???????????? ??????????????? ????????? ????????????.")
            void It_increases_review_count_and_returns_review() {
                User user = userService.increaseReviewCount(EXISTS_ID);

                assertThat(user.getReviewCount()).isEqualTo(3);
            }
        }
    }

    @Nested
    @DisplayName("decreaseReviewCount ????????????")
    class Describe_decreaseReviewCount {
        @Nested
        @DisplayName("????????? ????????? ???")
        class Context_when_exists_user {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .reviewCount(2)
                        .build();

                given(userRepository.findUserById(EXISTS_ID)).willReturn(Optional.of(user));
            }

            @Test
            @DisplayName("???????????? ?????? ???????????? ??????????????? ????????? ????????????.")
            void It_decreases_review_count_and_returns_review() {
                User user = userService.decreaseReviewCount(EXISTS_ID);

                assertThat(user.getReviewCount()).isEqualTo(1);
            }
        }
    }
}
