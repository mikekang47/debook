package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ReviewServiceTest {
    private static final Long EXISTS_USER_ID = 1L;
    private static final Long EXISTS_REVIEW_ID = 2L;
    private static final Long NOT_EXISTS_REVIEW_ID = 200L;
    private static final String TITLE = "good book";
    private static final String BODY = "It was a fantastic book ever.";

    private static final String KEYWORD = "코틀린 투 자바";

    @MockBean
    private UserService userService;

    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        reviewService = new ReviewService(userService, reviewRepository, mapper);
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
                ReviewRequestData reviewRequestData = new ReviewRequestData("좋은 책", "좋은 책이네요.", 7L, EXISTS_USER_ID);
                Review review = reviewService.createReview(EXISTS_USER_ID, reviewRequestData);

                assertThat(review.getTitle()).isEqualTo("좋은 책");
                assertThat(review.getBody()).isEqualTo("좋은 책이네요.");
                assertThat(review.getUserId()).isEqualTo(EXISTS_USER_ID);
            }
        }
    }

    @Nested
    @DisplayName("getReviewById 메서드는")
    class Describe_getReviewById {
        @Nested
        @DisplayName("Id와 일치하는 리뷰가 있을 때")
        class Context_when_exists_review {
            @BeforeEach
            void setUp() {
                Review review = Review.builder()
                        .id(EXISTS_REVIEW_ID)
                        .userId(EXISTS_USER_ID)
                        .body(BODY)
                        .title(TITLE)
                        .build();

                given(reviewRepository.findById(EXISTS_REVIEW_ID))
                        .willReturn(Optional.of(review));
            }

            @Test
            @DisplayName("리뷰를 반환한다.")
            void It_returns_review() {
                Review review = reviewService.getReviewById(EXISTS_REVIEW_ID);

                assertThat(review.getId()).isEqualTo(EXISTS_REVIEW_ID);
                assertThat(review.getTitle()).isEqualTo(TITLE);
                assertThat(review.getBody()).isEqualTo(BODY);
            }
        }

        @Nested
        @DisplayName("Id와 일치하는 리뷰가 없을 때")
        class Context_when_not_exists_review {
            @BeforeEach
            void setUp() {
                given(reviewRepository.findById(NOT_EXISTS_REVIEW_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("에러를 던진다.")
            void It_throws_error() {
                assertThatThrownBy(() -> reviewService.getReviewById(EXISTS_REVIEW_ID))
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("getReviewByName 메서드는")
    class Describe_getReviewByName {
        @Nested
        @DisplayName("keyword를 포함한 리뷰가 존재하고")
        class Context_when_review_exists {
            @Nested
            @DisplayName("날짜 순으로 조회할 때")
            class Context_when_sort_by_creating_date {
                @BeforeEach
                void setUp() {
                    Review review1 = Review.builder()
                            .id(EXISTS_REVIEW_ID)
                            .title(TITLE)
                            .body(BODY)
                            .createdAt(LocalDateTime.now().plusMinutes(40))
                            .build();

                    Review review2 = Review.builder()
                            .id(EXISTS_REVIEW_ID + 1)
                            .title(TITLE + "best of the best")
                            .body("second body")
                            .createdAt(LocalDateTime.now().plusMinutes(30))
                            .build();

                    given(reviewRepository.findReviewByDate(KEYWORD))
                            .willReturn(List.of(review1, review2));
                }

                @Test
                @DisplayName("날짜순으로 리뷰를 반환한다.")
                void It_returns_reviews_sorted_by_date() {
                    List<Review> reviews = reviewService.getReviewByKeyword(KEYWORD, "date");

                    assertThat(reviews.get(0).getTitle()).isEqualTo(TITLE);
                }
            }
        }
    }

    @Nested
    @DisplayName("updateReview")
    class Describe_updateReview {
        @Nested
        @DisplayName("존재하는 리뷰와 올바른 요청이 왔을 때")
        class Context_when_exists_review_and_valid_requests {
            @BeforeEach
            void setUp() {
                Review review = Review.builder()
                        .id(EXISTS_REVIEW_ID)
                        .title(TITLE)
                        .body(BODY)
                        .userId(EXISTS_USER_ID)
                        .build();

                given(reviewRepository.findById(EXISTS_REVIEW_ID)).willReturn(Optional.of(review));
            }

            @Test
            @DisplayName("업데이트된 리뷰를 반환한다.")
            void It_returns_updated_review() {
                ReviewRequestData reviewRequestData = new ReviewRequestData("새로 작성된 타이틀", "업데이트된 본문입니다. 10글자가 넘습니다.", 7L, EXISTS_USER_ID);

                Review review = reviewService.updateReview(EXISTS_REVIEW_ID, reviewRequestData, EXISTS_USER_ID);

                assertThat(review.getId()).isEqualTo(EXISTS_REVIEW_ID);
                assertThat(review.getTitle()).isEqualTo("새로 작성된 타이틀");
                assertThat(review.getBody()).contains("업데이트");
                assertThat(review.getUserId()).isEqualTo(EXISTS_USER_ID);
            }
        }
    }
}
