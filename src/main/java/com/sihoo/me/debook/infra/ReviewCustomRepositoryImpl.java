package com.sihoo.me.debook.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sihoo.me.debook.domains.Review;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.sihoo.me.debook.domains.QReview.review;

public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public ReviewCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Review> findReviewByDate(String keyword) {
        return jpaQueryFactory.selectFrom(review)
                .where(review.title.contains(keyword))
                .where(review.body.contains(keyword))
                .stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .collect(Collectors.toList());


    }

    @Override
    public List<Review> findReviewByCorrectness(String keyword) {
        // TODO
        // MongoDB 를 사용한 역 인덱싱 방식으로 검색 결과 반환
        return jpaQueryFactory.selectFrom(review)
                .where(review.title.contains(keyword))
                .where(review.body.contains(keyword))
                .stream().collect(Collectors.toList());
    }
}
