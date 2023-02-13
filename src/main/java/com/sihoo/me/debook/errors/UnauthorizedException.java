package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class UnauthorizedException extends CustomException {
	private final String message;

	public UnauthorizedException(Long userId) {
		super(ErrorCode.UNAUTHORIZED_EXCEPTION);
		this.message = ErrorCode.UNAUTHORIZED_EXCEPTION.getErrorMessage() + "(userId: " + userId + ")";
	}
}
