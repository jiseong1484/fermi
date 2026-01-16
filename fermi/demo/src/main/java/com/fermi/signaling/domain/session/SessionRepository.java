package com.fermi.signaling.domain.session;

import java.util.Optional;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findById(String sessionId);
    void end(String sessionId);
}