package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class InvalidReqBodyException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(RequestTimeoutException.class);

	public InvalidReqBodyException() {
		super();
	}

	public InvalidReqBodyException(String message) {
		super(message);
		LOGGER.error(message);
	}
}
