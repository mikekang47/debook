package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Review;

import java.util.List;

public interface ReviewCustomRepository {
    List<Review> findReviewByDate(String keyword);

    List<Review> findReviewByCorrectness(String keyword);

    boolean existsReviewById(Long reviewId);

    List<Review> findAllByBookId(Long bookId);
}
