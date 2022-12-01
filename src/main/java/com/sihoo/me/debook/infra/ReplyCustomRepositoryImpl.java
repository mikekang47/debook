package com.sihoo.me.debook.infra;

import com.querydsl.jpa.JPQLQueryFactory;
import com.sihoo.me.debook.domains.Reply;

import java.util.Optional;

import static com.sihoo.me.debook.domains.QReply.reply;

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
}
