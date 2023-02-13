package com.sihoo.me.debook.errors;

import javax.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class BookAlreadExistsException extends CustomException {
	private final String message;

	public BookAlreadExistsException(@NotNull String title, @NotNull String author, @NotNull Long isbn) {
		super(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION);
		this.message =
			ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION.getErrorMessage() + "(title: " + title + ", author: " + author
				+ ", isbn: " + isbn + ")";
	}
}
