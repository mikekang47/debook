package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ReplyServiceTest {
    private static final Long EXISTS_REVIEW_ID = 2L;
    private static final Long NOT_EXISTS_REVIEW_ID = 2L;
    private static final Long EXISTS_REPLY_ID = 1L;
    private static final Long EXISTS_USER_ID = 5L;

    private ReplyService replyService;
    @MockBean
    private UserService userService;

    @MockBean
    private ReviewService reviewService;

    private final ReplyRepository replyRepository = mock(ReplyRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        replyService = new ReplyService(userService, reviewService, replyRepository, mapper);
    }


    @Nested
    @DisplayName("createReply 메서드는")
    class Describe_createReply {
        @Nested
        @DisplayName("올바른 데이터가 왔을 때")
        class Context_when_valid_requests {
            @BeforeEach
            void setUp() {
                User user = User.builder()
                        .id(EXISTS_USER_ID)
                        .replyCount(2)
                        .build();

                given(reviewService.existsReview(EXISTS_REVIEW_ID)).willReturn(true);
                given(userService.increaseReplyCount(EXISTS_USER_ID)).willReturn(user);
                given(replyRepository.save(any(Reply.class))).will(invocation -> {
                    Reply reply = invocation.getArgument(0);
                    return Reply.builder()
                            .id(EXISTS_REPLY_ID)
                            .userId(reply.getUserId())
                            .message(reply.getMessage())
                            .reviewId(reply.getReviewId())
                            .build();
                });
            }

            @Test
            @DisplayName("생성된 리뷰를 반환한다.")
            void It_returns_reply() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 좋은 책입니까?");
                Reply reply = replyService.createReply(replyRequestData, EXISTS_REVIEW_ID, EXISTS_USER_ID);

                assertThat(reply.getMessage()).isEqualTo("이거 좋은 책입니까?");
                assertThat(reply.getUserId()).isEqualTo(EXISTS_USER_ID);
                assertThat(reply.getReviewId()).isEqualTo(EXISTS_REVIEW_ID);
            }
        }

        @Nested
        @DisplayName("리뷰가 존재하지 않을 때")
        class Context_when_review_not_exists {
            @BeforeEach
            void setUp() {
                given(reviewService.existsReview(NOT_EXISTS_REVIEW_ID)).willReturn(false);
            }

            @Test
            @DisplayName("not found 에러를 던진다.")
            void It_throws_error() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 좋은 책입니까?");
                assertThatThrownBy(() -> replyService.createReply(replyRequestData, NOT_EXISTS_REVIEW_ID, EXISTS_USER_ID))
                        .hasMessageContaining("[ERROR] Review not found")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

}