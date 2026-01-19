package com.sakthi.secureauth.config;

import com.sakthi.secureauth.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String path = request.getRequestURI();

        if (path.startsWith("/auth/login") || path.startsWith("/auth/refresh")) {

            String clientIp = getClientIp(request);

            boolean allowed = rateLimitService.isAllowed(clientIp);

            if (!allowed) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests. Please try again later.");
                return false;
            }
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
