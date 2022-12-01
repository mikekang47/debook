package com.sihoo.me.debook.infra;

import com.querydsl.jpa.JPQLQueryFactory;
import com.sihoo.me.debook.domains.Reply;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sihoo.me.debook.domains.QReply.reply;
import static com.sihoo.me.debook.domains.QReview.review;

public class ReplyCustomRepositoryImpl implements ReplyCustomRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    public ReplyCustomRepositoryImpl(JPQLQueryFactory jpqlQueryFactory) {
        this.jpqlQueryFactory = jpqlQueryFactory;
    }

    @Override
    public Optional<Reply> findReviewById(Long id) {
        return jpqlQueryFactory.selectFrom(reply)
                .where(reply.id.eq(id))
                .where(reply.isDeleted.eq(false))
                .stream().findFirst();
    }

    @Override
    public List<Reply> findAllByReviewId(Long id) {
        return jpqlQueryFactory.select(reply)
                .from(reply)
                .leftJoin(review)
                .on(review.id.eq(reply.reviewId))
                .where(review.id.eq(id))
                .stream()
                .collect(Collectors.toList());
    }
}
