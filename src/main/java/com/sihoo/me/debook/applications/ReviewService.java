package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.infra.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    public ReviewService(UserService userService, ReviewRepository reviewRepository) {
        this.userService = userService;
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(Long userId, ReviewRequestData reviewRequestData) {
        Review review = reviewRepository.save(reviewRequestData.toEntity(userId));
        userService.increaseReviewCount(userId);

        return review;
    }
}
