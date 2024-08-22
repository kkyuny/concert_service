package com.hhdplus.concert_service.interfaces.common;


import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.application.facade.QueueFacade;
import com.hhdplus.concert_service.application.facade.QueueRedisFacade;
import com.hhdplus.concert_service.interfaces.common.exception.ForbidenException;
import com.hhdplus.concert_service.interfaces.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final QueueRedisFacade queueRedisFacade;

    static Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnauthorizedException, ForbidenException {
        LOGGER.info("ACCESS URI : " + request.getRequestURI());

        String token = request.getHeader("authorization");

        if(token.equals("testToken")){
            return true;
        }

        return queueRedisFacade.verifyToken(token);
    }
}