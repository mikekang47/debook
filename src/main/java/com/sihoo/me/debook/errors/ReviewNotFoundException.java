package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class ReviewNotFoundException extends CustomException {
	private final String message;
	public ReviewNotFoundException(Long reviewId) {
		super(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.REVIEW_NOT_FOUND_EXCEPTION.getErrorMessage() + "(reviewId: " + reviewId + ")";
	}
}
