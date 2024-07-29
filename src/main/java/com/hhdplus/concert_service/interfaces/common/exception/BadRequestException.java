package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class BadRequestException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(BadRequestException.class);

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super(message);
		LOGGER.error(message);
	}

}
