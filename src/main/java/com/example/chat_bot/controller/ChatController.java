package com.example.chat_bot.controller;

import com.example.chat_bot.redis.Publisher;
import com.example.chat_bot.redis.SubscriberHelper;
import com.example.chat_bot.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private Publisher redisPublisher;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SubscriberHelper subscriberHelper;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String userId, @RequestParam String messageContent) {
        try {
            // Lưu tin nhắn của người dùng vào cơ sở dữ liệu
            messageService.saveMessage(userId, messageContent, "user");
            logger.info("User message saved to DB and sent to Redis channel {}: {}", userId, messageContent);

            // Publish user message to Redis
            redisPublisher.publish(userId, messageContent);

            // Gửi phản hồi của bot sau 2 giây
            taskExecutor.submit(() -> sendBotResponseWithDelay(userId));

            return ResponseEntity.ok("User message sent, bot will respond after 2 seconds.");
        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message.");
        }
    }

    @Async
    public void sendBotResponseWithDelay(String userId) {
        try {
            // Đợi 2 giây trước khi bot phản hồi
            Thread.sleep(2000);

            // Bot generates a response
            String botResponse = subscriberHelper.getRandomBotResponse();

            // Lưu phản hồi của bot vào cơ sở dữ liệu
            messageService.saveMessage(userId, botResponse, "bot");

            // Publish bot response to Redis
            redisPublisher.publish(userId, botResponse);
            logger.info("Bot response saved to DB and sent to Redis channel {}: {}", userId, botResponse);

        } catch (InterruptedException e) {
            logger.error("Thread interrupted while waiting to send bot response: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing bot response: {}", e.getMessage());
        }
    }
}

