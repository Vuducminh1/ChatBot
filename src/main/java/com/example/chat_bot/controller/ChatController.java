package com.example.chat_bot.controller;

import com.example.chat_bot.model.Message;
import com.example.chat_bot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8888") // URL của frontend
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody String content) {
        Message savedMessage = chatService.saveUserMessage(content);
        chatService.scheduleRandomBotReply();
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(chatService.getAllMessages());
    }
}