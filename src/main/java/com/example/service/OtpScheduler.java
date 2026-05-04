package com.example.service;

import com.example.dao.OtpDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OtpScheduler.class);
    private final OtpDao otpDao;

    public OtpScheduler(OtpDao otpDao) {
        this.otpDao = otpDao;
    }

    @Scheduled(fixedRate = 60000)
    public void markExpiredCodes() {
        logger.debug("Scheduler running: checking for expired OTP codes...");
        otpDao.expireOutdatedCodes();
    }
}
