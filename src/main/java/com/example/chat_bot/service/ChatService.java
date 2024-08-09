package com.example.chat_bot.service;

import com.example.chat_bot.model.Message;
import com.example.chat_bot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public ChatService(MessageRepository messageRepository, StringRedisTemplate redisTemplate) {
        this.messageRepository = messageRepository;
        this.redisTemplate = redisTemplate;
    }

    public Message saveUserMessage(String content) {
        Message message = new Message();
        message.setContent(content);
        message.setSender("USER");
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public void scheduleRandomBotReply() {
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
            String randomReply = getRandomReply();
            Message botMessage = new Message();
            botMessage.setContent(randomReply);
            botMessage.setSender("BOT");
            botMessage.setTimestamp(LocalDateTime.now());
            messageRepository.save(botMessage);
        });
    }

    private String getRandomReply() {
        return redisTemplate.opsForSet().randomMember("bot_replies");
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByTimestampDesc();
    }
}