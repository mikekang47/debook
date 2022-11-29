package com.sihoo.me.debook.dto;

import com.sihoo.me.debook.domains.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class ReviewRequestData {
    @NotEmpty
    private String title;

    @NotEmpty
    @Size(min=10)
    private String body;

    public Review toEntity(Long userId) {
        return Review.builder()
                .title(this.title)
                .body(this.body)
                .userId(userId)
                .build();
    }
}
