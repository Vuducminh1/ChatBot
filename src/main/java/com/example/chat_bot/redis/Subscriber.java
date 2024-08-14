package com.example.chat_bot.redis;

import com.example.chat_bot.websockets.WebSocketSessionManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Subscriber {

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

    private RedisPubSubCommands<String, String> sync;

    // Khai báo biến logger
    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);

    public Subscriber(WebSocketSessionManager webSocketSessionManager) {
        this.webSocketSessionManager = webSocketSessionManager;
        RedisClient client = RedisClient.create("redis://localhost:6379");

        try {
            StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
            var redisListener = new SubscriberHelper(this.webSocketSessionManager);
            connection.addListener(redisListener);
            this.sync = connection.sync();
        } catch (RedisException e) {
            logger.error("Failed to connect to Redis for subscription: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while setting up Redis subscription: {}", e.getMessage());
        }
    }

    public void subscribe(String channel) {
        try {
            this.sync.subscribe(channel);
        } catch (RedisException e) {
            logger.error("Failed to subscribe to Redis channel {}: {}", channel, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while subscribing to Redis channel {}: {}", channel, e.getMessage());
        }
    }

    public void unsubscribe(String channel) {
        try {
            this.sync.unsubscribe(channel);
        } catch (RedisException e) {
            logger.error("Failed to unsubscribe from Redis channel {}: {}", channel, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while unsubscribing from Redis channel {}: {}", channel, e.getMessage());
        }
    }
}
