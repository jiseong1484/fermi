package com.fermi.signaling.application.session;

import com.fermi.signaling.common.config.AppProperties;
import com.fermi.signaling.common.util.SessionIdGenerator;
import com.fermi.signaling.domain.session.Session;
import com.fermi.signaling.domain.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final AppProperties props;

    public SessionService(SessionRepository sessionRepository, AppProperties props) {
        this.sessionRepository = sessionRepository;
        this.props = props;
    }

    public Session createSession(String agentId, String customerId) {
        if (agentId == null || agentId.isBlank()) throw new IllegalArgumentException("agentId is required");
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("customerId is required");
        // 충돌 가능성 거의 없지만, 안전하게 재시도
        for (int i = 0; i < 5; i++) {
            String id = SessionIdGenerator.generate(10);
            Instant now = Instant.now();
            Instant expiresAt = now.plus(props.getSessionTtlMinutes(), ChronoUnit.MINUTES);

            Session session = new Session(id, agentId, customerId, now, expiresAt);
            // in-memory라 충돌 체크가 단순하지만, 나중 DB에서도 동일하게 갈 수 있게 구조 유지
            sessionRepository.save(session);
            return session;
        }
        throw new IllegalStateException("Failed to generate unique sessionId");
    }

    public Session getSessionOrThrow(String sessionId) {
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // 조회 시점에 만료 갱신
        s.expireIfNeeded(Instant.now());
        return s;
    }

    public String getFrontendBaseUrl() {
        return props.getFrontendBaseUrl();
    }
}