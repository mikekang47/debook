package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {
	private final String message;

	public UserNotFoundException(Long userId) {
		super(ErrorCode.USER_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.USER_NOT_FOUND_EXCEPTION.getErrorMessage() + "(userId: " + userId + ")";
	}

	public UserNotFoundException(String email) {
		super(ErrorCode.USER_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.USER_NOT_FOUND_EXCEPTION.getErrorMessage() + "(userId: " + email + ")";
	}
}
