package com.sihoo.me.debook.applications;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.errors.NoAuthorityReviewException;
import com.sihoo.me.debook.errors.ReviewNotFoundException;
import com.sihoo.me.debook.infra.ReviewRepository;

@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final Mapper mapper;

    public ReviewService(UserService userService, ReviewRepository reviewRepository, Mapper mapper) {
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Review createReview(Long userId, ReviewRequestData reviewRequestData) {
        Review review = reviewRepository.save(mapper.map(reviewRequestData,Review.class));
        userService.increaseReviewCount(userId);

        return review;
    }

    public Review getReviewById(Long id) {
        return findReview(id);
    }

    public List<Review> getReviewByKeyword(String keyword, String type) {
        if (type.equals("date")) {
            return reviewRepository.findReviewByDate(keyword);
        }
        return reviewRepository.findReviewByCorrectness(keyword);

    }

    @Transactional
    public Review updateReview(Long id, ReviewRequestData reviewRequestData, Long userId) {
        Review review = findReview(id);

        authorize(userId, review);

        review.changeWith(mapper.map(reviewRequestData, Review.class));

        return review;
    }

    @Transactional
    public Review deleteReview(Long id, Long userId) {
        Review review = findReview(id);

        authorize(userId, review);

        reviewRepository.delete(review);

        userService.decreaseReviewCount(userId);

        return review;
    }

    private Review findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    private static void authorize(Long userId, Review review) {
        if (!Objects.equals(userId, review.getUserId())) {
            throw new NoAuthorityReviewException(userId);
        }
    }

    public boolean existsReview(Long reviewId) {
        return reviewRepository.existsReviewById(reviewId);
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findAllByBookId(bookId);
    }
}
