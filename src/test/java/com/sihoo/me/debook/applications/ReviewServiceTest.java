package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.infra.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ReviewServiceTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REVIEW_ID = 2L;

    @MockBean
    private UserService userService;

    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(userService, reviewRepository);
    }

    @Nested
    @DisplayName("createReview 메서드는")
    class Describe_createReview {
        @Nested
        @DisplayName("올바른 요청이 들어왔을 때")
        class Context_when_valid_requests {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_USER_ID)
                        .reviewCount(1)
                        .build();

                given(userService.increaseReviewCount(EXISTS_USER_ID)).willReturn(user);
                given(reviewRepository.save(any(Review.class))).will(invocation -> {
                    Review review = invocation.getArgument(0);
                    return Review.builder()
                            .id(EXISTS_REVIEW_ID)
                            .userId(EXISTS_USER_ID)
                            .title(review.getTitle())
                            .body(review.getBody())
                            .build();
                });
            }

            @Test
            void It_returns_review() {
                ReviewRequestData reviewRequestData = new ReviewRequestData("좋은 책", "좋은 책이네요.");
                Review review = reviewService.createReview(EXISTS_USER_ID, reviewRequestData);

                assertThat(review.getTitle()).isEqualTo("좋은 책");
                assertThat(review.getBody()).isEqualTo("좋은 책이네요.");
            }
        }
    }
}
