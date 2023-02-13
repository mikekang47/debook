package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class BookNotFoundException extends CustomException {
	private final String message;
	public BookNotFoundException(Long id) {
		super(ErrorCode.BOOK_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.BOOK_NOT_FOUND_EXCEPTION.getErrorMessage() + "(Book id: " + id + ")";
	}
}
