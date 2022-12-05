package com.sihoo.me.debook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookResponse implements Serializable {
    private Long total;
    private Long start;
    private List<Item> items = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Item {
        private String title;
        private String author;
        private String publisher;
        private Long isbn;
    }
}
