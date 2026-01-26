package com.fermi.signaling.application.session;

import com.fermi.signaling.common.config.AppProperties;
import com.fermi.signaling.common.util.SessionIdGenerator;
import com.fermi.signaling.domain.session.Session;
import com.fermi.signaling.domain.session.SessionRepository;
import com.fermi.signaling.domain.session.SessionEvent;
import com.fermi.signaling.domain.session.SessionEventRepository;
import com.fermi.signaling.domain.session.SessionSummary;
import com.fermi.signaling.domain.session.SessionSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionEventRepository sessionEventRepository;
    private final SessionSummaryRepository sessionSummaryRepository;
    private final AppProperties props;

    public SessionService(
        SessionRepository sessionRepository,
        SessionEventRepository sessionEventRepository,
        SessionSummaryRepository sessionSummaryRepository,
        AppProperties props
    ) {
        this.sessionRepository = sessionRepository;
        this.sessionEventRepository = sessionEventRepository;
        this.sessionSummaryRepository = sessionSummaryRepository;
        this.props = props;
    }

    @Transactional
    public Session createSession(String agentId, String customerId) {
        if (agentId == null || agentId.isBlank()) throw new IllegalArgumentException("agentId is required");
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("customerId is required");

        for (int i = 0; i < 5; i++) {
            String id = SessionIdGenerator.generate(10);
            // Check for collision before attempting to create
            if (sessionRepository.findBySessionId(id).isEmpty()) {
                Instant now = Instant.now();
                Instant expiresAt = now.plus(props.getSessionTtlMinutes(), ChronoUnit.MINUTES);
                Session session = new Session(id, agentId, customerId, now, expiresAt);
                return sessionRepository.save(session);
            }
        }
        throw new IllegalStateException("Failed to generate unique sessionId");
    }

    @Transactional
    public Session getSessionOrThrow(String sessionId) {
        Session s = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // 조회 시점에 만료 갱신. @Transactional에 의해 변경 감지되어 자동 저장됨.
        s.expireIfNeeded(Instant.now());
        return s;
    }

    public String getFrontendBaseUrl() {
        return props.getFrontendBaseUrl();
    }

    @Transactional
    public Session endSessionOrThrow(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId is required");
        }

        Session s = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        s.end();
        logEvent(sessionId, "SESSION_ENDED", null);
        return s; // @Transactional에 의해 변경 감지되어 자동 저장됨
    }

    @Transactional(readOnly = true)
    public java.util.List<Session> getAllSessions() {
        return sessionRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

    @Transactional
    public void logEvent(String sessionId, String eventType, String details) {
        Session session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found for event logging"));
        
        SessionEvent event = new SessionEvent(session, eventType, details);
        sessionEventRepository.save(event);
    }

    @Transactional
    public SessionSummary saveSummary(String sessionId, String content) {
        Session session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found for summary"));
        
        SessionSummary summary = new SessionSummary(session, content);
        return sessionSummaryRepository.save(summary);
    }
}