package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class NoAuthorityReviewException extends CustomException {
	private final String message;
	public NoAuthorityReviewException(Long reviewId) {
		super(ErrorCode.NO_AUTHORITY_REVIEW_EXCEPTION);
		this.message = ErrorCode.NO_AUTHORITY_REVIEW_EXCEPTION.getErrorMessage() + "(reviewId: " + reviewId + ")";
	}
}
