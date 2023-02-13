package com.sihoo.me.debook.errors;

import lombok.Getter;

@Getter
public class SortTypeNotFoundException extends CustomException{
	private final String message;
	public SortTypeNotFoundException(String type) {
		super(ErrorCode.SORT_TYPE_NOT_FOUND_EXCEPTION);
		this.message = ErrorCode.SORT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage() + "(type :" + type + ")";
	}
}
