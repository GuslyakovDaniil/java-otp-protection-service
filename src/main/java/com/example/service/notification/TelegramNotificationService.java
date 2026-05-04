package com.example.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class TelegramNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    public void sendCode(String destination, String code) {
        String message = String.format("Operation for %s, your OTP code is: %s", destination, code);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                botToken, chatId, urlEncode(message));

        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Telegram API error. Status code: {}", response.statusCode());
            } else {
                logger.info("Telegram message sent successfully");
            }
        } catch (InterruptedException e) {
            logger.error("Error sending Telegram message: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("Error sending Telegram message: {}", e.getMessage());
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
