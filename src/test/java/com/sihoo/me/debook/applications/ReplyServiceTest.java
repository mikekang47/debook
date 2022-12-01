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

import java.util.List;
import java.util.Optional;

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
    private static final Long NOT_EXISTS_REPLY_ID = 200L;
    private static final Long EXISTS_USER_ID = 5L;
    private static final Long NEW_REPLY_ID = 0L;

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
        @DisplayName("첫 댓글을 생성하면서, 올바른 데이터가 왔을 때")
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
                            .targetReplyId(reply.getTargetReplyId())
                            .reviewId(reply.getReviewId())
                            .build();
                });
            }

            @Test
            @DisplayName("생성된 리뷰를 반환한다.")
            void It_returns_reply() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 좋은 책입니까?");
                Reply reply = replyService.createReply(replyRequestData, EXISTS_REVIEW_ID, NEW_REPLY_ID, EXISTS_USER_ID);

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
                assertThatThrownBy(() -> replyService.createReply(replyRequestData, NOT_EXISTS_REVIEW_ID, NEW_REPLY_ID, EXISTS_USER_ID))
                        .hasMessageContaining("[ERROR] Review not found")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("getReplyById 메서드는")
    class Describe_getReplyById {
        @Nested
        @DisplayName("댓글이 존재할 때")
        class Context_when_exists_review {
            @BeforeEach
            void setUp() {
                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .build();

                given(replyRepository.findReviewById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
            }

            @Test
            @DisplayName("댓글을 반환한다.")
            void It_returns_reply() {
                Reply reply = replyService.getReplyById(EXISTS_REPLY_ID);

                assertThat(reply.getId()).isEqualTo(EXISTS_REPLY_ID);
            }
        }

        @Nested
        @DisplayName("댓글이 존재하지 않을 때")
        class Context_when_not_exists_review {
            @BeforeEach
            void setUp() {
                given(replyRepository.findReviewById(NOT_EXISTS_REPLY_ID)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("NotFound 에러를 던진다.")
            void It_throws_not_found_error() {
                assertThatThrownBy(() -> replyService.getReplyById(NOT_EXISTS_REPLY_ID))
                        .hasMessageContaining("[ERROR] Reply not found")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("getRepliesByReviewId 메서드는")
    class Describe_getRepliesByReviewId {
        @Nested
        @DisplayName("리뷰의 댓글이 존재할 때")
        class Context_when_reply_exists {
            @BeforeEach
            void setUp() {
                Reply reply1 = Reply.builder()
                        .reviewId(EXISTS_REVIEW_ID)
                        .build();
                Reply reply2 = Reply.builder()
                        .reviewId(EXISTS_REVIEW_ID)
                        .build();

                given(replyRepository.findAllByReviewId(EXISTS_REVIEW_ID)).willReturn(List.of(reply1, reply2));
            }

            @Test
            @DisplayName("댓글을 전부 반환한다.")
            void It_returns_replies() {
                List<Reply> replies = replyService.getRepliesByReviewId(EXISTS_REVIEW_ID);

                assertThat(replies.get(0).getReviewId()).isEqualTo(EXISTS_REVIEW_ID);
            }
        }
    }
}