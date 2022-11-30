package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.ReviewService;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.domains.SortType;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RequestMapping("/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Review create(@RequestBody @Valid ReviewRequestData reviewRequestData,
                         UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();

        return reviewService.createReview(userId, reviewRequestData);
    }

    @GetMapping("/{id}")
    public Review detailById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/search/{keyword}")
    public List<Review> detailByKeyword(@PathVariable String keyword, @RequestParam String sortType) {
        return reviewService.getReviewByKeyword(keyword, SortType.from(sortType).getType());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Review update(@PathVariable Long id,
                         @RequestBody @Valid ReviewRequestData reviewRequestData,
                         UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();
        return reviewService.updateReview(id, reviewRequestData, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public void delete(@PathVariable Long id, UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();
        reviewService.deleteReview(id, userId);
    }
}
