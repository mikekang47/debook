package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.RoleRepository;
import com.sihoo.me.debook.infra.UserRepository;
import com.sihoo.me.debook.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {
    private static final Long EXISTS_ID = 1L;
    private static final String EXISTS_EMAIL = "exists@email.com";
    private static final String NOT_EXISTS_EMAIL = "notexists@email.com";
    private static final String EXISTS_PASSWORD = "validpassword";
    private static final String NOT_EXISTS_PASSWORD = "nopassword";
    private static final String WRONG_PASSWORD = "wrongpassword";
    private static final String SECRET = "thisapplicationisfordeveloperwholovesbook";
    private static final String VALID_ACCESS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    private AuthenticationService authenticationService;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userRepository, jwtUtil, roleRepository);
    }

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {
        @Nested
        @DisplayName("존재하는 email과 올바른 비밀번호가 주어졌을 떄")
        class Context_when_gives_exists_email_and_valid_password {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .email(EXISTS_EMAIL)
                        .password(EXISTS_PASSWORD)
                        .isDeleted(false)
                        .build();

                given(userRepository.findByEmail(EXISTS_EMAIL)).willReturn(Optional.of(user));
            }

            @Test
            @DisplayName("토큰을 리턴한다.")
            void It_returns_access_token() {
                String token = authenticationService.login(EXISTS_EMAIL, EXISTS_PASSWORD);

                assertThat(token).isEqualTo(VALID_ACCESS_TOKEN);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 email과 비밀번호가 주어졌을 때")
        class Context_when_gives_not_exists_email_and_password {
            @BeforeEach
            void setUp() {
                given(userRepository.findByEmail(NOT_EXISTS_EMAIL))
                        .willThrow(new CustomException("User not found(Email: " + NOT_EXISTS_EMAIL + ")",
                                HttpStatus.NOT_FOUND));
            }

            @Test
            @DisplayName("로그인 실패 에러를 던진다.")
            void It_throws_login_fail_exception() {
                assertThatThrownBy(() -> authenticationService.login(NOT_EXISTS_EMAIL, NOT_EXISTS_PASSWORD))
                        .hasMessageContaining("User not found")
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("존재하는 이메일과 틀린 비밀번호가 주어졌을 때")
        class Context_when_gives_exists_email_and_wrong_password {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_ID)
                        .email(EXISTS_EMAIL)
                        .password(EXISTS_PASSWORD)
                        .isDeleted(false)
                        .build();

                given(userRepository.findByEmail(EXISTS_EMAIL)).willReturn(Optional.of(user));

            }

            @Test
            @DisplayName("비밀번호 에러를 던진다.")
            void It_throws_login_fail_exception() {
                assertThatThrownBy(() -> authenticationService.login(EXISTS_EMAIL, WRONG_PASSWORD))
                        .hasMessageContaining("Login failed. Password doesn't match")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

}
