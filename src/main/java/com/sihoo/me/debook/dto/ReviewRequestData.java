package com.sihoo.me.debook.dto;

import com.github.dozermapper.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class ReviewRequestData {
    @NotEmpty
    @Mapping("title")
    private String title;

    @NotEmpty
    @Size(min=10)
    @Mapping("body")
    private String body;

    @Mapping("bookId")
    private long bookId;

    @Mapping("userId")
    private long userId;
}
