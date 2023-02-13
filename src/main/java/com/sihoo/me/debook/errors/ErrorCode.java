package com.sihoo.me.debook.errors;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "U-001", "[ERROR] User not found"),
	UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "A-002", "[ERROR] Unauthorized User"),
	PASSWORD_NOT_MATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "P-001", "[ERROR] Password not match"),
	BOOK_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "B-001", "[ERROR] Book not found"),
	BOOK_ALREADY_EXISTS_EXCEPTION(HttpStatus.BAD_REQUEST, "B-002", "[ERROR] Book already exists"),
	REPLY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "R-001", "[ERROR] Reply not found"),
	NO_AUTHORITY_REPLY_EXCEPTION(HttpStatus.FORBIDDEN, "R-002", "[ERROR] No authority for reply"),
	REVIEW_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "RV-001", "[ERROR] Review not found"),
	NO_AUTHORITY_REVIEW_EXCEPTION(HttpStatus.FORBIDDEN, "RV-002", "[ERROR] No authority for review"),
	INVALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "T-001", "[ERROR] Invalid Token"),
	SORT_TYPE_NOT_FOUND_EXCEPTION(HttpStatus.BAD_REQUEST, "S-001", "[ERROR] Sort type not found");

	private final HttpStatus status;
	private final String internalErrorCode;
	private final String errorMessage;
}
