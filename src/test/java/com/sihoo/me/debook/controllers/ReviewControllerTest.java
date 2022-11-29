package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.AuthenticationService;
import com.sihoo.me.debook.applications.ReviewService;
import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.ReviewRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REVIEW_ID = 2L;
    private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;


    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {
        @Nested
        @DisplayName("권한이 있을 때")
        class Context_has_authority {
            @Nested
            @DisplayName("올바른 요청이 들어오면")
            class Context_when_valid_requests {
                @BeforeEach
                void setUp() {
                    given(authenticationService.getRoles(EXISTS_USER_ID))
                            .willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));

                    given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);

                    User user = User.builder()
                            .id(EXISTS_USER_ID)
                            .reviewCount(1)
                            .build();

                    given(userService.increaseReviewCount(EXISTS_USER_ID)).willReturn(user);

                    given(reviewService.createReview(eq(EXISTS_USER_ID), any(ReviewRequestData.class))).will(invocation -> {
                        ReviewRequestData reviewRequestData = invocation.getArgument(1);
                        return Review.builder()
                                .id(EXISTS_REVIEW_ID)
                                .userId(EXISTS_USER_ID)
                                .title(reviewRequestData.getTitle())
                                .body(reviewRequestData.getBody())
                                .build();
                    });
                }

                @Test
                @DisplayName("201과 생성된 리뷰를 응답한다.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/reviews")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"title\":\"good book\", \"body\": \"It was fantastic book.\"}")
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andExpect(status().isCreated());
                }
            }
        }
    }
}
