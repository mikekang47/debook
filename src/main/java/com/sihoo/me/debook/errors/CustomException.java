package com.sihoo.me.debook.errors;

import lombok.Getter;

public class CustomException extends RuntimeException {
	@Getter
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
