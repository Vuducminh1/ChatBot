package com.example.chat_bot.redis;

import com.example.chat_bot.service.MessageService;
import com.example.chat_bot.websockets.WebSocketSessionManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriberHelper implements RedisPubSubListener<String, String> {

    private final WebSocketSessionManager webSocketSessionManager;
    private static final Logger logger = LoggerFactory.getLogger(SubscriberHelper.class);
    private final String[] botResponses = {
            "Hello, I'm your bot. How can I help you today?",
            "I'm a bot. Nice to meet you!",
            "Here's a random response from the bot.",
            "Thanks for your message. I'll get back to you soon!",
            "Bot here! What can I do for you?"
    };

    @Autowired
    private MessageService messageService;

    public SubscriberHelper(WebSocketSessionManager webSocketSessionManager) {
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void message(String channel, String message) {
        logger.info("Received message on channel {}: {}", channel, message);

        try {
            // Lưu tin nhắn của người dùng vào cơ sở dữ liệu
            messageService.saveMessage(channel, message, "user");

            // Tạo phản hồi của bot
            String botResponse = getRandomBotResponse();

            // Gửi phản hồi của bot trở lại Redis
            logger.info("Publishing bot response to Redis channel {}: {}", channel, botResponse);
            try (var connection = RedisClient.create("redis://localhost:6379").connect()) {
                RedisCommands<String, String> syncCommands = connection.sync();
                syncCommands.publish(channel, botResponse);
            }

            // Lưu phản hồi của bot vào cơ sở dữ liệu
            messageService.saveMessage(channel, botResponse, "bot");
            logger.info("Bot response saved to database: {}", botResponse);

        } catch (RedisException e) {
            logger.error("Failed to publish bot response to Redis channel {}: {}", channel, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while processing message from Redis channel {}: {}", channel, e.getMessage());
        }
    }


    public String getRandomBotResponse() {
        int index = (int) (Math.random() * botResponses.length);
        return botResponses[index];
    }

    @Override
    public void message(String s, String k1, String s2) {}

    @Override
    public void subscribed(String s, long l) {}

    @Override
    public void psubscribed(String s, long l) {}

    @Override
    public void unsubscribed(String s, long l) {}

    @Override
    public void punsubscribed(String s, long l) {}
}
