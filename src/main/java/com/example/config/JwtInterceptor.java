package com.example.config;

import com.example.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Пропускаем публичные эндпоинты аутентификации
        if (uri.startsWith("/api/auth/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized access attempt to {}", uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            logger.warn("Invalid or expired token used for {}", uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
            return false;
        }

        String role = jwtUtil.extractRole(token);
        String username = jwtUtil.extractUsername(token);

        // Проверка ролей (Разграничение доступа)
        if (uri.startsWith("/api/admin") && !"ADMIN".equals(role)) {
            logger.warn("User '{}' attempted to access ADMIN API: {}", username, uri);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Admins only");
            return false;
        }

        // Сохраняем имя пользователя в запросе, чтобы контроллер мог его использовать
        request.setAttribute("username", username);
        return true;
    }
}
