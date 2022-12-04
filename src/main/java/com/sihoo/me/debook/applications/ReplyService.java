package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.ReplyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@Transactional(readOnly = true)
public class ReplyService {
    private final UserService userService;
    private final ReviewService reviewService;
    private final ReplyRepository replyRepository;
    private final Mapper mapper;


    public ReplyService(UserService userService,
                        ReviewService reviewService,
                        ReplyRepository replyRepository,
                        Mapper mapper) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Reply createReply(
            ReplyRequestData replyRequestData,
            Long reviewId,
            Long targetReplyId,
            Long authorId) {
        checkReviewExists(reviewId);

        Reply reply = replyRequestData.toEntity(reviewId, targetReplyId, authorId);
        userService.increaseReplyCount(authorId);

        return replyRepository.save(reply);
    }

    public Reply getReplyById(Long id) {
        return findReply(id);
    }

    public List<Reply> getRepliesByReviewId(Long id) {
        return replyRepository.findAllByReviewId(id);
    }

    public List<Reply> getMyReplies(Long userId) {
        return replyRepository.findAllByUserId(userId);
    }

    @Transactional
    public Reply updateReply(Long id, ReplyRequestData replyRequestData, Long userId) {
        Reply reply = findReply(id);

        authorize(id, userId, reply);

        reply.changeMessage(mapper.map(replyRequestData, Reply.class));

        return reply;
    }

    @Transactional
    public Reply deleteReply(Long id, Long userId) {
        Reply reply = findReply(id);

        authorize(id, userId, reply);

        reply.deleteReply();

        return reply;
    }

    private Reply findReply(Long id) {
        return replyRepository.findReplyById(id)
                .orElseThrow(() -> new CustomException("[ERROR] Reply not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    private void checkReviewExists(Long reviewId) {
        boolean existsReview = reviewService.existsReview(reviewId);

        if (!existsReview) {
            throw new CustomException("[ERROR] Review not found(Id: " + reviewId + ")", HttpStatus.NOT_FOUND);
        }
    }

    private static void authorize(Long id, Long userId, Reply reply) {
        if (!Objects.equals(reply.getUserId(), userId)) {
            throw new CustomException("[ERROR] No authorization for reply(Id: " + id + ", userId: " + userId + ")",
                    HttpStatus.UNAUTHORIZED);
        }
    }
}
