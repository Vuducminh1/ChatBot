package com.example.chat_bot.service;


import com.example.chat_bot.redis.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private Publisher publisher;

    public void publishMessage(String channel, String message) {
        // Gửi tin nhắn đến Redis
        publisher.publish(channel, message);
    }
}
