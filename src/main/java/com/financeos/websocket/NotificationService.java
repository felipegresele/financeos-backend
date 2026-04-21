package com.financeos.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(String userId, String event, Object payload) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                Map.of("event", event, "data", payload)
            );
        } catch (Exception e) {
            log.warn("WebSocket notification failed for user {}: {}", userId, e.getMessage());
        }
    }

    public void broadcast(String event, Object payload) {
        messagingTemplate.convertAndSend("/topic/global", Map.of("event", event, "data", payload));
    }
}
