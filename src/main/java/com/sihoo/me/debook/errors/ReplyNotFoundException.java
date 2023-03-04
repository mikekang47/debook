package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class ReplyNotFoundException extends CustomException {
	private final String message;
	public ReplyNotFoundException(Long id) {
		super(ErrorCode.REPLY_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.REPLY_NOT_FOUND_EXCEPTION.getErrorMessage() + "(id: " + id + ")";
	}
}
