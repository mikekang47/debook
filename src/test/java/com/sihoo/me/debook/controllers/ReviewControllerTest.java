package com.sihoo.me.debook.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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
import com.sihoo.me.debook.applications.ReviewService;
import com.sihoo.me.debook.applications.UserService;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.errors.ReviewNotFoundException;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REVIEW_ID = 2L;
    private static final Long NOT_EXISTS_REVIEW_ID = 200L;
    private static final String EXISTS_TOKEN = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ" +
            ".eyJ1c2VySWQiOjF9.xShOSgEwVSlvgg699JR4ieN8k3thMgbuDcV_rKEA8dA";

    private static final String TITLE = "good book";
    private static final String BODY = "It was a fantastic book ever.";

    private static final String KEYWORD = "코틀린 투 자바";
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
                                .bookId(reviewRequestData.getBookId())
                                .build();
                    });
                }

                @Test
                @DisplayName("201과 생성된 리뷰를 응답한다.")
                void It_responds_201_and_review() throws Exception {
                    mvc.perform(post("/reviews")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"title\":\"good book\", \"body\": \"It was fantastic book.\", \"bookId\": 7}")
                                    .header("Authorization", "Bearer " + EXISTS_TOKEN)
                            )
                            .andDo(print())
                            .andExpect(status().isCreated());
                }
            }
        }
    }

    @Nested
    @DisplayName("detailById 메서드는")
    class Describe_detailById {
        @Nested
        @DisplayName("id와 일치하는 리뷰가 있을 때")
        class Context_when_exists_review {
            @BeforeEach
            void setUp() {
                Review review = Review.builder()
                        .title(TITLE)
                        .body(BODY)
                        .userId(EXISTS_USER_ID)
                        .build();

                given(reviewService.getReviewById(EXISTS_REVIEW_ID))
                        .willReturn(review);
            }

            @Test
            @DisplayName("200과 리뷰를 응답한다.")
            void It_responds_200_and_review() throws Exception {
                mvc.perform(get("/reviews/" + EXISTS_REVIEW_ID))
                        .andExpect(content().string(containsString(TITLE)))
                        .andExpect(status().isOk());

            }
        }

        @Nested
        @DisplayName("id와 일치하는 리뷰가 있을 때")
        class Context_when_not_exists_review {
            @BeforeEach
            void setUp() {
                given(reviewService.getReviewById(NOT_EXISTS_REVIEW_ID)).willThrow(new ReviewNotFoundException(NOT_EXISTS_REVIEW_ID));
            }

            @Test
            @DisplayName("404를 응답한다.")
            void It_responds_404() throws Exception {
                mvc.perform(get("/reviews/" + NOT_EXISTS_REVIEW_ID))
                        .andExpect(status().isNotFound());

            }
        }
    }

    @Nested
    @DisplayName("detailByKeyword 메서드는")
    class Describe_detailByKeyword {
        @Nested
        @DisplayName("keyword가 존재하고, 날짜 순으로 조회할 경우")
        class Context_when_keyword_exists_and_sort_by_created_date {
            @BeforeEach
            void setUp() {
                Review review1 = Review.builder()
                        .title(KEYWORD + "이거 정말 좋은 책")
                        .createdAt(LocalDateTime.now().plusHours(3))
                        .build();

                Review review2 = Review.builder()
                        .title(KEYWORD + "오히려 좋아")
                        .createdAt(LocalDateTime.now().plusHours(4))
                        .build();

                given(reviewService.getReviewByKeyword(KEYWORD, "date"))
                        .willReturn(List.of(review2, review1));
            }

            @Test
            @DisplayName("200과 최신순의 리뷰를 응답한다.")
            void It_responds_200_and_reviews() throws Exception {
                mvc.perform(get("/reviews/search/" + KEYWORD)
                                .param("sortType", "date")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                        )
                        .andExpect(content().string(containsString(KEYWORD)))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("upate메서드는")
    class Describe_update {
        @Nested
        @DisplayName("올바른 reviewId, 올바른 요청이 주어졌고, 작성자와 사용자가 같을 때")
        class Context_when_valid_review_Id_and_valid_requests_and_same_user {
            @BeforeEach
            void setUp() {
                given(authenticationService.getRoles(EXISTS_USER_ID)).willReturn(List.of(new Role(1L, EXISTS_USER_ID, RoleType.USER)));
                given(authenticationService.parseToken(EXISTS_TOKEN)).willReturn(EXISTS_USER_ID);
                given(reviewService.updateReview(eq(EXISTS_REVIEW_ID), any(ReviewRequestData.class), eq(EXISTS_USER_ID)))
                        .will(invocation -> {
                            ReviewRequestData reviewRequestData = invocation.getArgument(1);
                            return Review.builder()
                                    .id(EXISTS_REVIEW_ID)
                                    .userId(EXISTS_USER_ID)
                                    .title(reviewRequestData.getTitle())
                                    .body(reviewRequestData.getBody())
                                    .bookId(reviewRequestData.getBookId())
                                    .build();
                        });
            }

            @Test
            @DisplayName("200과 리뷰를 반환한다")
            void It_responds_200_and_review() throws Exception {
                mvc.perform(patch("/reviews/" + EXISTS_REVIEW_ID)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .content("{\"title\":\"수정된 타이틀\", \"body\":\"수정된 바디입니다. 10글자 채우기\", \"bookId\":7}")
                                .header("Authorization", "Bearer " + EXISTS_TOKEN)
                        )
                        .andDo(print())
                        .andExpect(status().isOk());
            }

        }
    }
}
