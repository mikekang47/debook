package com.sihoo.me.debook.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class JwtUtilTest {

    private static final Long EXISTS_ID = 1L;
    private static final String SECRET = "thisapplicationisfordeveloperwholovesbook";

    private static final String VALID_ACCESSTOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";
    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @Test
    void encodeTest() {
        String accessToken = jwtUtil.encode(EXISTS_ID);

        assertThat(accessToken).isEqualTo(VALID_ACCESSTOKEN);
    }
}
