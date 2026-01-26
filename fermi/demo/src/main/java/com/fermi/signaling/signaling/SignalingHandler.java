package com.fermi.signaling.signaling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper om = new ObjectMapper();

    // roomId -> (그 방에 들어온 웹소켓 세션들)
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    // userId (agentId or customerId) -> (웹소켓 세션)
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트 연결 시점에 사용자 ID를 받을 수 있다면 여기서 userSessions에 추가 가능
        // 현재는 첫 메시지 (register)에서 받는 것으로 가정
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 연결이 끊기면 어떤 방에 있든 제거
        rooms.values().forEach(set -> set.remove(session));

        // userSessions에서도 제거
        userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root = om.readTree(message.getPayload());

        String type = root.path("type").asText("");
        String roomId = root.path("roomId").asText("");
        String userId = root.path("userId").asText(""); // userId 필드 추가

        if (type.isBlank()) return;

        switch (type) {
            case "register" -> { // 새로운 register 타입 추가
                if (!userId.isBlank()) {
                    userSessions.put(userId, session);
                    System.out.println("User registered: " + userId + " with session " + session.getId());

                    // Send confirmation back to the client
                    Map<String, Object> response = Map.of("type", "register-success");
                    session.sendMessage(new TextMessage(om.writeValueAsString(response)));
                }
            }
            case "join" -> {
                if (roomId.isBlank()) return;
                Set<WebSocketSession> roomParticipants = rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
                roomParticipants.add(session);
                System.out.println("Session " + session.getId() + " joined room " + roomId);

                // If room now has 2 participants, send peer_ready to both
                if (roomParticipants.size() == 2) {
                    Map<String, Object> readyMessage = Map.of("type", "peer_ready", "roomId", roomId);
                    TextMessage textReadyMessage = new TextMessage(om.writeValueAsString(readyMessage));
                    for (WebSocketSession s : roomParticipants) {
                        if (s.isOpen()) {
                            s.sendMessage(textReadyMessage);
                        }
                    }
                    System.out.println("Room " + roomId + " is full. Sent peer_ready to both participants.");
                }
            }
            case "leave" -> {
                if (roomId.isBlank()) return;
                Set<WebSocketSession> set = rooms.get(roomId);
                if (set != null) set.remove(session);
                System.out.println("Session " + session.getId() + " left room " + roomId);
            }
            case "offer", "answer", "candidate", "ended" -> {
                if (roomId.isBlank()) return;
                relayToOthers(roomId, session, message);
            }
            default -> {
                // ignore
                System.out.println("Unknown message type: " + type);
            }
        }
    }

    private void relayToOthers(String roomId, WebSocketSession sender, TextMessage msg) throws Exception {
        Set<WebSocketSession> set = rooms.get(roomId);
        if (set == null) {
            System.out.println("Room " + roomId + " not found for relay.");
            return;
        }

        for (WebSocketSession s : set) {
            if (!s.isOpen()) continue;
            if (s.getId().equals(sender.getId())) continue;
            s.sendMessage(msg); // 메시지 그대로 브로드캐스트(보낸 사람 제외)
        }
    }

    /**
     * 특정 사용자에게 메시지를 보냅니다.
     * @param userId 메시지를 받을 사용자 ID (agentId 또는 customerId)
     * @param message 보낼 객체 (JSON으로 변환하여 전송)
     * @throws IOException 메시지 전송 중 오류 발생 시
     */
    public void sendMessageToUser(String userId, Object message) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(om.writeValueAsString(message)));
            System.out.println("Sent message to user " + userId + ": " + om.writeValueAsString(message));
        } else {
            System.out.println("User " + userId + " not found or session not open.");
        }
    }
}