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
            @DisplayName("생성된 댓글을 반환한다.")
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

                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
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
                given(replyRepository.findReplyById(NOT_EXISTS_REPLY_ID)).willReturn(Optional.empty());
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

    @Nested
    @DisplayName("getMyReplies 메서드는")
    class Describe_getMyReplies {
        @Nested
        @DisplayName("사용자가 작성한 댓글이 있을 때")
        class Context_when_reply_exists {
            @BeforeEach
            void setUp() {
                Reply reply1 = Reply.builder()
                        .userId(EXISTS_USER_ID)
                        .message("첫번째 메시지")
                        .build();
                Reply reply2 = Reply.builder()
                        .userId(EXISTS_USER_ID)
                        .message("두번째 메시지")
                        .build();

                given(replyRepository.findAllByUserId(EXISTS_USER_ID)).willReturn(List.of(reply1, reply2));
            }

            @Test
            @DisplayName("댓글을 모두 반환한다.")
            void It_returns_all_replies() {
                List<Reply> replies = replyService.getMyReplies(EXISTS_USER_ID);

                assertThat(replies).isNotEmpty();
                assertThat(replies.get(0).getUserId()).isEqualTo(EXISTS_USER_ID);
            }
        }

        @Nested
        @DisplayName("사용자가 작성한 댓글이 없을 때")
        class Context_when_reply_not_exists {
            @BeforeEach
            void setUp() {
                given(replyRepository.findAllByUserId(EXISTS_USER_ID)).willReturn(List.of());
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void It_returns_empty_list() {
                List<Reply> replies = replyService.getMyReplies(EXISTS_USER_ID);

                assertThat(replies).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("updateReply 메서드는")
    class Describe_updateReply {
        @Nested
        @DisplayName("댓글의 사용자 id와 현재 사용자 id가 같을 때")
        class Context_when_same_user {
            @BeforeEach
            void setUp() {
                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .message("이건 수정전 댓글임")
                        .userId(EXISTS_USER_ID)
                        .build();

                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
            }

            @Test
            @DisplayName("수정된 댓글을 반환한다.")
            void It_returns_updated_reply() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 수정된 댓글임.");
                Reply reply = replyService.updateReply(EXISTS_REPLY_ID, replyRequestData, EXISTS_USER_ID);

                assertThat(reply.getMessage()).isEqualTo("이거 수정된 댓글임.");
                assertThat(reply.getUserId()).isEqualTo(EXISTS_USER_ID);
            }
        }

        @Nested
        @DisplayName("댓글의 사용자 id와 현재 사용자 id가 다를 때")
        class Context_when_different_user {
            @BeforeEach
            void setUp() {
                final Long DIFF_USER_ID = 10L;

                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .message("이건 수정전 댓글임")
                        .userId(DIFF_USER_ID)
                        .build();

                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
            }

            @Test
            @DisplayName("수정된 댓글을 반환한다.")
            void It_returns_unauthorized_error() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 수정된 댓글임.");
                assertThatThrownBy(() -> replyService.updateReply(EXISTS_REPLY_ID, replyRequestData, EXISTS_USER_ID))
                        .hasMessageContaining("[ERROR] No authority for reply")
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("댓글이 존재하지 않을 때")
        class Context_when_not_exist_reply {
            @BeforeEach
            void setUp() {
                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("not found 에러를 던진다.")
            void It_throws_not_found_error() {
                ReplyRequestData replyRequestData = new ReplyRequestData("이거 수정된 댓글임.");
                assertThatThrownBy(() -> replyService.updateReply(EXISTS_REPLY_ID, replyRequestData, EXISTS_USER_ID))
                        .hasMessageContaining("[ERROR] Reply not found")
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteReply 메서드는")
    class Describe_deleteReply {
        @BeforeEach
        void setUp() {
            Reply reply = Reply.builder()
                    .id(EXISTS_REPLY_ID)
                    .userId(EXISTS_USER_ID)
                    .build();

            given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
        }

        @Nested
        @DisplayName("댓글이 존재하고 작성한 사용자가 같을 때")
        class Context_when_reply_exists_and_same_user {
            @Test
            @DisplayName("댓글의 상태를 변경시키고 댓글을 리턴한다.")
            void It_returns_reply_and_change_status() {
                Reply reply = replyService.deleteReply(EXISTS_REPLY_ID, EXISTS_USER_ID);

                assertThat(reply.isDeleted()).isTrue();
            }
        }

        @Nested
        @DisplayName("댓글이 존재하지 않는다면")
        class Context_when_reply_not_exists {
            @BeforeEach
            void setUp() {
                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("not found 에러를 던진다.")
            void It_throws_not_found_error() {
                assertThatThrownBy(() -> replyService.deleteReply(EXISTS_REPLY_ID, EXISTS_USER_ID))
                        .hasMessageContaining("[ERROR] Reply not found")
                        .isInstanceOf(CustomException.class);


            }
        }

        @Nested
        @DisplayName("댓글 작성자가 다르다면")
        class Context_when_different_user {
            @BeforeEach
            void setUp() {
                Reply reply = Reply.builder()
                        .id(EXISTS_REPLY_ID)
                        .userId(EXISTS_USER_ID)
                        .build();

                given(replyRepository.findReplyById(EXISTS_REPLY_ID)).willReturn(Optional.of(reply));
            }

            @Test
            @DisplayName("unauthorized 에러를 던진다.")
            void It_throws_unauthorized_error() {
                final Long differentUserId = 12L;
                assertThatThrownBy(() -> replyService.deleteReply(EXISTS_REPLY_ID, differentUserId))
                        .hasMessageContaining("[ERROR] No authority for reply")
                        .isInstanceOf(CustomException.class);
            }
        }
    }
}
