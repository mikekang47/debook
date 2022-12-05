package com.sihoo.me.debook.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sihoo.me.debook.domains.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<User> findUserByNickName(String nickName) {
        return jpaQueryFactory.selectFrom(user)
                .where(user.isDeleted.eq(false))
                .where(user.nickName.contains(nickName))
                .stream()
                .sorted(Comparator.comparing(User::getNickName))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsers() {
        return jpaQueryFactory.selectFrom(user)
                .where(user.isDeleted.eq(false))
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaQueryFactory.selectFrom(user)
                .where(user.isDeleted.eq(false))
                .where(user.email.eq(email))
                .stream()
                .findFirst();
    }
}
