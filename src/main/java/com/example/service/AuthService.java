package com.example.service;

import com.example.dao.UserDao;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.dto.AuthRequest;
import com.example.model.dto.AuthResponse;
import com.example.util.JwtUtil;
import com.example.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    public void register(AuthRequest request) {
        logger.debug("Attempting to register user: {}", request.getUsername());
        Role role = Role.valueOf(request.getRole().toUpperCase());

        if (role == Role.ADMIN && userDao.hasAdmin()) {
            throw new RuntimeException("Admin already exists in the system!");
        }
        if (userDao.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("User already exists!");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        User user = new User(0, request.getUsername(), hashedPassword, role);
        userDao.save(user);
        logger.info("User registered successfully: {}", request.getUsername());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userDao.findByUsername(request.getUsername());
        if (user == null || !PasswordUtil.checkPassword(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials!");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        logger.info("User logged in: {}", user.getUsername());
        return new AuthResponse(token);
    }
}
