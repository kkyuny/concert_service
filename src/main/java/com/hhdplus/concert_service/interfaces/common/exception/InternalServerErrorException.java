package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error")
public class InternalServerErrorException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(InvalidReqBodyException.class);

	public InternalServerErrorException() {
		super();
	}

	public InternalServerErrorException(String message) {
		super(message);
		LOGGER.error(message);
	}

}