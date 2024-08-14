package com.example.chat_bot.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

    private RedisClient client;
    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    public Publisher() {
        this.client = RedisClient.create("redis://localhost:6379");
    }

    public void publish(String channel, String message) {
        logger.info("Going to publish the message to channel {} and message = {}", channel, message);
        try (var connection = this.client.connect()) {
            connection.sync().publish(channel, message);
        } catch (RedisException e) {
            logger.error("Failed to publish message to Redis channel {}: {}", channel, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while publishing message to Redis channel {}: {}", channel, e.getMessage());
        }
    }
}
