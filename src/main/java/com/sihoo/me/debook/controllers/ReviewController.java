package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.ReviewService;
import com.sihoo.me.debook.domains.Review;
import com.sihoo.me.debook.dto.ReviewRequestData;
import com.sihoo.me.debook.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

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
    public Review create(@RequestBody @Valid ReviewRequestData reviewRequestData, UserAuthentication userAuthentication) {
        Long userId = userAuthentication.getUserId();

        return reviewService.createReview(userId, reviewRequestData);
    }
}
