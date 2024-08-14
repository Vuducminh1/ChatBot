package com.example.chat_bot.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> webSocketSessionByUserId = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionManager.class);

    public void addWebSocketSession(WebSocketSession webSocketSession) {
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        logger.info("Adding WebSocket session for user id: {} with session id: {}", userId, webSocketSession.getId());
        this.webSocketSessionByUserId.put(userId, webSocketSession);
    }

    public void removeWebSocketSession(WebSocketSession webSocketSession) {
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(webSocketSession);
        logger.info("Removing WebSocket session for user id: {} with session id: {}", userId, webSocketSession.getId());
        this.webSocketSessionByUserId.remove(userId);
    }

    public WebSocketSession getWebSocketSession(String userId) {
        return this.webSocketSessionByUserId.get(userId);
    }
}
