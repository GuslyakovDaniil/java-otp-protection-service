package com.example.service;

import com.example.dao.OtpConfigDao;
import com.example.dao.OtpDao;
import com.example.dao.UserDao;
import com.example.model.OtpCode;
import com.example.model.OtpConfig;
import com.example.model.OtpStatus;
import com.example.model.dto.OtpRequest;
import com.example.model.dto.OtpValidateRequest;
import com.example.service.notification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final OtpDao otpDao;
    private final OtpConfigDao configDao;
    private final UserDao userDao;

    // Каналы рассылки
    private final EmailNotificationService emailService;
    private final SmppNotificationService smppService;
    private final TelegramNotificationService telegramService;
    private final FileNotificationService fileService;

    public OtpService(OtpDao otpDao, OtpConfigDao configDao, UserDao userDao,
                      EmailNotificationService emailService, SmppNotificationService smppService,
                      TelegramNotificationService telegramService, FileNotificationService fileService) {
        this.otpDao = otpDao;
        this.configDao = configDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.smppService = smppService;
        this.telegramService = telegramService;
        this.fileService = fileService;
    }

    public void generateAndSendOtp(String username, OtpRequest request) {
        int userId = userDao.findByUsername(username).getId();
        OtpConfig config = configDao.getConfig();

        // Генерация случайного кода нужной длины
        String code = generateRandomCode(config.getCodeLength());
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (config.getTtlSeconds() * 1000L));

        OtpCode otpCode = new OtpCode(0, userId, request.getOperationId(), code, OtpStatus.ACTIVE, expiresAt, null);
        otpDao.save(otpCode);
        logger.info("Generated OTP for user '{}', operation '{}'", username, request.getOperationId());

        // Маршрутизация по каналу
        switch (request.getDeliveryChannel().toUpperCase()) {
            case "EMAIL" -> emailService.sendCode(request.getDestination(), code);
            case "SMS" -> smppService.sendCode(request.getDestination(), code);
            case "TELEGRAM" -> telegramService.sendCode(request.getDestination(), code);
            case "FILE" -> fileService.sendCode(request.getDestination(), code);
            default -> throw new IllegalArgumentException("Unknown delivery channel");
        }
    }

    public boolean validateOtp(String username, OtpValidateRequest request) {
        int userId = userDao.findByUsername(username).getId();
        OtpCode otpCode = otpDao.findActiveByOperationId(request.getOperationId(), userId);

        if (otpCode == null) {
            logger.warn("Validation failed: No active code found for operation '{}'", request.getOperationId());
            return false;
        }

        if (otpCode.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
            otpDao.updateStatus(otpCode.getId(), OtpStatus.EXPIRED);
            logger.info("Validation failed: Code expired for operation '{}'", request.getOperationId());
            return false;
        }

        if (otpCode.getCode().equals(request.getCode())) {
            otpDao.updateStatus(otpCode.getId(), OtpStatus.USED);
            logger.info("OTP Validated successfully for operation '{}'", request.getOperationId());
            return true;
        }

        return false;
    }

    private String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}