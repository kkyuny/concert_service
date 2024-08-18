package com.hhdplus.concert_service.interfaces.common;


import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.application.facade.QueueFacade;
import com.hhdplus.concert_service.interfaces.common.exception.ForbidenException;
import com.hhdplus.concert_service.interfaces.common.exception.UnauthorizedException;
import com.hhdplus.concert_service.interfaces.dto.request.QueueRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class TokenInterceptor implements HandlerInterceptor {

	@Autowired
	QueueFacade queueFacade;

	static Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);

    @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnauthorizedException, ForbidenException {
		LOGGER.info("ACCESS URI : " + request.getRequestURI());

		String token = request.getHeader("authorization");
		queueFacade.verifyToken(token); // token이 없으면 exception 발생

		return true;
	}
}
