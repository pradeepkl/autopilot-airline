package com.autopilot.airline.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Echoes the caller's X-Correlation-Id, or generates one, so a request can be traced end to end. */
public class CorrelationIdInterceptor implements HandlerInterceptor {

    static final String HEADER = "X-Correlation-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String id = request.getHeader(HEADER);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", id);
        response.setHeader(HEADER, id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        MDC.remove("correlationId");
    }
}
