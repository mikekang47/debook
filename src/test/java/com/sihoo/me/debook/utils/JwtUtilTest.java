package com.sihoo.me.debook.utils;

import com.sihoo.me.debook.errors.CustomException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
class JwtUtilTest {

    private static final Long EXISTS_ID = 1L;
    private static final String SECRET = "thisapplicationisfordeveloperwholovesbook";

    private static final String VALID_ACCESSTOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    private static final String INVALID_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2basdfQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dB";
    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @Test
    void encodeTest() {
        String accessToken = jwtUtil.encode(EXISTS_ID);

        assertThat(accessToken).isEqualTo(VALID_ACCESSTOKEN);
    }

    @Nested
    @DisplayName("decode 메서드는")
    class Describe_decode {
        @Nested
        @DisplayName("올바른 token이라면")
        class Context_when_valid_token {
            @Test
            @DisplayName("올바른 Claims를 반환한다.")
            void It_returns_claims() {
                Claims claims = jwtUtil.decode(VALID_ACCESSTOKEN);

                assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("올바르지 않은 token이라면")
        class Context_when_invalid_token {
            @Test
            @DisplayName("에러를 발생한다.")
            void It_throws_error() {
                assertThatThrownBy(() -> jwtUtil.decode(INVALID_TOKEN))
                        .hasMessageContaining("[ERROR]")
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("token이 null이라면")
        class Context_when_null_token {
            private final String NULL_TOKEN = null;

            @Test
            @DisplayName("에러를 발생한다.")
            void It_throws_error() {
                assertThatThrownBy(() -> jwtUtil.decode(NULL_TOKEN))
                        .hasMessageContaining("[ERROR]")
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("token이 빈 토큰이라면")
        class Context_when_blank_token {
            private final String BLANK_TOKEN = "";

            @Test
            @DisplayName("에러를 발생한다.")
            void It_throws_error() {
                assertThatThrownBy(() -> jwtUtil.decode(BLANK_TOKEN))
                        .hasMessageContaining("[ERROR]")
                        .isInstanceOf(CustomException.class);
            }
        }
    }
}
