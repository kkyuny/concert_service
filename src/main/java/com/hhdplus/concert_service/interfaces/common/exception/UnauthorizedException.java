package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
public class UnauthorizedException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(UnauthorizedException.class);

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message) {
		super(message);
		LOGGER.error(message);
	}

}
