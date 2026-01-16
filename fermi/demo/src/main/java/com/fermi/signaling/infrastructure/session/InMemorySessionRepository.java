package com.fermi.signaling.infrastructure.session;

import com.fermi.signaling.domain.session.Session;
import com.fermi.signaling.domain.session.SessionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySessionRepository implements SessionRepository {
    private final ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();

    @Override public void save(Session session) { map.put(session.getSessionId(), session); }

    @Override public Optional<Session> findById(String sessionId) { return Optional.ofNullable(map.get(sessionId)); }

    @Override public void end(String sessionId) {
        Session s = map.get(sessionId);
        if (s != null) s.end();
    }
}