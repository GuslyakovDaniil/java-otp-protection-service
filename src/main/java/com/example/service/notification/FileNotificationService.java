package com.example.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FileNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(FileNotificationService.class);

    public void sendCode(String destination, String code) {
        String fileName = "otp_codes.txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(LocalDateTime.now() + " - To: " + destination + " | Code: " + code + "\n");
            logger.info("OTP Code saved to file {} for {}", fileName, destination);
        } catch (IOException e) {
            logger.error("Failed to write code to file", e);
        }
    }
}
