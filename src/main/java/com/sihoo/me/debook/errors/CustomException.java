package com.sihoo.me.debook.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    // TODO
    // ErroraAdice 만들기
    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
