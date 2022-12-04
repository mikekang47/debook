package com.sihoo.me.debook.dto;

import com.github.dozermapper.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Builder
public class BookRequestData {
    @NotNull
    @Mapping("title")
    private String title;

    @NotNull
    @Mapping("author")
    private String author;

    @NotNull
    @Mapping("publisher")
    private String publisher;

    @NotNull
    @Mapping("isbn")
    private Long isbn;

    @Mapping("imageUrl")
    private String imageUrl;
}
