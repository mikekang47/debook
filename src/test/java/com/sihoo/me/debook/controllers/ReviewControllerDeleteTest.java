package com.sihoo.me.debook.controllers;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.applications.ReviewService;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.errors.ReviewNotFoundException;

@WebMvcTest(ReviewController.class)
public class ReviewControllerDeleteTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REVIEW_ID = 2L;
    private static final Long NOT_EXISTS_REVIEW_ID = 200L;
    private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ReviewService reviewService;

    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {
        @Nested
        @DisplayName("리뷰가 존재할 때")
        class Context_when_review_exists {
            @BeforeEach
            void setUp() {
                Review review = Review.builder().build();

                given(authenticationService.getRoles(EXISTS_USER_ID))
                        .willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                given(reviewService.deleteReview(EXISTS_REVIEW_ID, EXISTS_USER_ID)).willReturn(review);
            }

            @Test
            @DisplayName("204를 응답한다.")
            void It_responds_204() throws Exception {
                mvc.perform(delete("/reviews/" + EXISTS_REVIEW_ID)
                                .header("Authorization", "Bearer " + EXISTS_TOKEN)
                        )
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("리뷰가 존재하지 않을 때")
        class Context_when_review_not_exists {
            @BeforeEach
            void setUp() {
                given(authenticationService.getRoles(EXISTS_USER_ID))
                        .willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                given(reviewService.deleteReview(NOT_EXISTS_REVIEW_ID, EXISTS_USER_ID)).willThrow(
                        new ReviewNotFoundException(NOT_EXISTS_REVIEW_ID)
                );
            }

            @Test
            @DisplayName("404를 응답한다.")
            void It_responds_404() throws Exception {
                mvc.perform(delete("/reviews/" + NOT_EXISTS_REVIEW_ID)
                                .header("Authorization", "Bearer " + EXISTS_TOKEN)
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("자격이 없을 때")
        class Context_when_no_authority {
            @Test
            @DisplayName("401을 응답한다.")
            void It_responds_401() throws Exception {
                mvc.perform(delete("/reviews/" + EXISTS_REVIEW_ID)
                        )
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}
