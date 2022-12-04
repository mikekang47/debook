package com.sihoo.me.debook.dto;

import com.github.dozermapper.core.Mapping;
import com.sihoo.me.debook.domains.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ReviewRequestData {
    @NotEmpty
    @Mapping("title")
    private String title;

    @NotEmpty
    @Size(min=10)
    @Mapping("body")
    private String body;

    public Review toEntity(Long userId) {
        return Review.builder()
                .title(this.title)
                .body(this.body)
                .userId(userId)
                .build();
    }
}
