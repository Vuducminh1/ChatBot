package com.example.chat_bot.websockets;

import com.example.chat_bot.redis.Publisher;
import com.example.chat_bot.redis.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class SocketTextHandler extends TextWebSocketHandler {


    private WebSocketSessionManager webSocketSessionManager;

    private Publisher redisPublisher;

    private Subscriber redisSubscriber;

    private static final Logger logger = LoggerFactory.getLogger(SocketTextHandler.class);

    public SocketTextHandler(WebSocketSessionManager webSocketSessionManager, Publisher redisPublisher, Subscriber redisSubscriber) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.redisPublisher = redisPublisher;
        this.redisSubscriber = redisSubscriber;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.webSocketSessionManager.addWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        this.redisSubscriber.subscribe(userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.webSocketSessionManager.removeWebSocketSession(session);
        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        this.redisSubscriber.unsubscribe(userId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        String[] payLoadSplit = payload.split("->");

        String conversationId = payLoadSplit[0].trim(); // Conversation ID
        String targetUserId = payLoadSplit[1].trim();   // Target User ID
        String messageToBeSent = payLoadSplit[2].trim(); // Message

        String userId = WebSocketHelper.getUserIdFromSessionAttribute(session);
        logger.info("got the payload {} and going to send to channel {}", payload, targetUserId);

        // Publish to the Redis channel with conversation ID
        this.redisPublisher.publish(conversationId + "-" + targetUserId, userId + ":" + messageToBeSent);
    }

}
