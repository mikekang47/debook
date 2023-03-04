package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomException {
	private final String message;
	public InvalidTokenException(String token) {
		super(ErrorCode.INVALID_TOKEN_EXCEPTION);
		this.message = ErrorCode.INVALID_TOKEN_EXCEPTION.getErrorMessage() + "(token :" + token + ")";
	}
}
