package com.hhdplus.concert_service.interfaces.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT, reason = "Request Timeout")
public class RequestTimeoutException extends RuntimeException{

	static Logger LOGGER = LoggerFactory.getLogger(InvalidReqBodyException.class);

	public RequestTimeoutException() {
		super();
	}

	public RequestTimeoutException(String message) {
		super(message);
		LOGGER.error(message);
	}

}