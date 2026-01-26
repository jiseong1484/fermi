package com.fermi.signaling.api.session.dto;

import com.fermi.signaling.domain.session.Session;
import com.fermi.signaling.domain.session.SessionStatus;

import java.time.Instant;

public record SessionDto(
    String sessionId,
    String agentId,
    String customerId,
    SessionStatus status,
    Instant createdAt,
    Instant expiresAt
) {
    public static SessionDto fromEntity(Session session) {
        return new SessionDto(
            session.getSessionId(),
            session.getAgentId(),
            session.getCustomerId(),
            session.getStatus(),
            session.getCreatedAt(),
            session.getExpiresAt()
        );
    }
}
