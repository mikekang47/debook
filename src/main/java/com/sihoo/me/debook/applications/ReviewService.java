package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    public ReviewService(UserService userService, ReviewRepository reviewRepository) {
        this.userService = userService;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review createReview(Long userId, ReviewRequestData reviewRequestData) {
        Review review = reviewRepository.save(reviewRequestData.toEntity(userId));
        userService.increaseReviewCount(userId);

        return review;
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException("[ERROR] Review not found(Id: " + id + ")", HttpStatus.NOT_FOUND));
    }

    public List<Review> getReviewByKeyword(String keyword, String type) {
        if(type.equals("date")) {
            return reviewRepository.findReviewByDate(keyword);
        }
        return reviewRepository.findReviewByCorrectness(keyword);

    }
}
