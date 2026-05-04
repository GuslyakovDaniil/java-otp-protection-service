package com.example.controller;

import com.example.model.dto.OtpRequest;
import com.example.model.dto.OtpValidateRequest;
import com.example.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/otp")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final OtpService otpService;

    public UserController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        logger.info("POST /api/user/otp/generate - User: {}, Operation: {}", username, request.getOperationId());
        try {
            otpService.generateAndSendOtp(username, request);
            return ResponseEntity.ok("OTP generated and sent via " + request.getDeliveryChannel());
        } catch (Exception e) {
            logger.error("Error generating OTP", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidateRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        logger.info("POST /api/user/otp/validate - User: {}, Operation: {}", username, request.getOperationId());

        boolean isValid = otpService.validateOtp(username, request);
        if (isValid) {
            return ResponseEntity.ok("OTP is valid and operation confirmed");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP");
        }
    }
}
