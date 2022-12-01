package com.sihoo.me.debook.applications;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Reply;
import com.sihoo.me.debook.dto.ReplyRequestData;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.ReplyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReplyService {
    private final UserService userService;
    private final ReviewService reviewService;
    private final ReplyRepository replyRepository;
    private final Mapper mapper;


    public ReplyService(UserService userService, ReviewService reviewService, ReplyRepository replyRepository, Mapper mapper) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Reply createReply(ReplyRequestData replyRequestData, Long reviewId, Long userId) {
        boolean existsReview = reviewService.existsReview(reviewId);

        if (!existsReview) {
            throw new CustomException("[ERROR] Review not found(Id: " + reviewId + ")", HttpStatus.NOT_FOUND);
        }

        Reply reply = replyRequestData.toEntity(reviewId, userId);
        userService.increaseReplyCount(userId);

        return replyRepository.save(reply);
    }
}
