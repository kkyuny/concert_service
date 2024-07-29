package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
public class ForbidenException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(ForbidenException.class);

	public ForbidenException() {
		super();
	}

	public ForbidenException(String message) {
		super(message);
		LOGGER.error(message);
	}

}
