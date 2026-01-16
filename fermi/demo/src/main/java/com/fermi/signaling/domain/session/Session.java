package com.fermi.signaling.domain.session;

import java.time.Instant;

public class Session {
    private final String sessionId;
    private final Instant createdAt;
    private final Instant expiresAt;
    private SessionStatus status;
    private final String agentId;
    private final String customerId;

    public Session(String sessionId, String agentId, String customerId, Instant createdAt, Instant expiresAt) {
        this.sessionId = sessionId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = SessionStatus.MATCHED;
        this.agentId = agentId;
        this.customerId = customerId;
    }

    public String getSessionId() { return sessionId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public SessionStatus getStatus() { return status; }
    public String getAgentId() {
        return agentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public void expireIfNeeded(Instant now) {
        if (status == SessionStatus.ACTIVE && isExpired(now)) {
            status = SessionStatus.EXPIRED;
        }
    }

    public void end() {
        status = SessionStatus.ENDED;
    }
}