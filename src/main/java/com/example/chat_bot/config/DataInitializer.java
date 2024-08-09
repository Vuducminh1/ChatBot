package com.example.chat_bot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public DataInitializer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        Set<String> replies = new HashSet<>(Arrays.asList(
                "Xin chào!",
                "Rất vui được gặp bạn.",
                "Bạn khỏe không?",
                "Thời tiết hôm nay thế nào?"

        ));
        redisTemplate.opsForSet().add("bot_replies", replies.toArray(new String[0]));
    }
}
