package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class PasswordNotMatchException extends CustomException {
	private final String message;
	public PasswordNotMatchException(String email) {
		super(ErrorCode.PASSWORD_NOT_MATCH_EXCEPTION);
		this.message = ErrorCode.PASSWORD_NOT_MATCH_EXCEPTION.getErrorMessage() + "(email: " + email + ")";
	}
}
