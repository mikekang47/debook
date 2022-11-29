package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.dto.ErrorResponse;
import com.sihoo.me.debook.errors.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerErrorAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleRestResponseException(CustomException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getMessage());
    }
}
