package com.hhdplus.concert_service.interfaces.common;

import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TokenFilter implements Filter {
    static Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, InvalidReqBodyException {
        LOGGER.info("token filter start");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();
        if (path.contains("/api/token/create")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String token = request.getHeader("authorization");
            if (token.isEmpty()) {
                throw new InvalidReqBodyException("Token is required.");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }

        LOGGER.info("token filter finish");
    }

    @Override
    public void destroy() {
    }
}
