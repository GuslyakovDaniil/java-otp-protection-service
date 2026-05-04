package com.example.service;

import com.example.dao.OtpConfigDao;
import com.example.dao.UserDao;
import com.example.model.User;
import com.example.model.dto.ConfigUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final OtpConfigDao configDao;
    private final UserDao userDao;

    public AdminService(OtpConfigDao configDao, UserDao userDao) {
        this.configDao = configDao;
        this.userDao = userDao;
    }

    public void updateConfig(ConfigUpdateRequest request) {
        configDao.updateConfig(request.getCodeLength(), request.getTtlSeconds());
        logger.info("OTP config updated: Length={}, TTL={}", request.getCodeLength(), request.getTtlSeconds());
    }

    public List<User> getAllUsersExceptAdmins() {
        return userDao.findAllUsersExceptAdmins();
    }

    public void deleteUser(int id) {
        userDao.deleteById(id);
        logger.info("Deleted user with ID: {}", id);
    }
}