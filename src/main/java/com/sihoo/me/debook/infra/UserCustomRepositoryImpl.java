package com.sihoo.me.debook.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sihoo.me.debook.domains.User;

import java.util.Optional;

import static com.sihoo.me.debook.domains.QUser.user;

public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public UserCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return jpaQueryFactory.selectFrom(user)
                .where(user.isDeleted.eq(false))
                .where(user.id.eq(id))
                .stream().findFirst();
    }
}
