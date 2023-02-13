package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class NoAuthorityReplyException extends CustomException {
	private final String message;
	public NoAuthorityReplyException(Long replyId, Long userId) {
		super(ErrorCode.NO_AUTHORITY_REPLY_EXCEPTION);
		this.message = ErrorCode.NO_AUTHORITY_REPLY_EXCEPTION.getErrorMessage() + "(replyId: " + replyId + ", userId: " + userId + ")";
	}
}
