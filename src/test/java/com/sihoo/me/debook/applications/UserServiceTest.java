package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.UserRequestData;
import com.sihoo.me.debook.infra.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private UserService userService;

    private static final Long NEW_ID = 5L;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Nested
    @DisplayName("createUser 메서드는")
    class Describe_createUser {
        @Nested
        @DisplayName("올바른 요청이 들어왔을 때")
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
            @DisplayName("유저를 생성한다.")
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

}
