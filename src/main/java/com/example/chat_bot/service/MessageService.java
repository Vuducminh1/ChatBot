package com.example.chat_bot.service;

import com.example.chat_bot.model.Message;
import com.example.chat_bot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(String conversationId, String content, String sender) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getMessages(String conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }
}

