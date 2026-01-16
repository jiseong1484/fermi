package com.fermi.signaling.application.matching;

import com.fermi.signaling.common.util.SessionIdGenerator;
import com.fermi.signaling.domain.agent.*;
import com.fermi.signaling.domain.queue.*;
import com.fermi.signaling.domain.session.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class MatchingService {
    private final AgentRepository agentRepository;
    private final QueueRepository queueRepository;
    private final SessionRepository sessionRepository;
    private final SessionIdGenerator idGen;

    private final ReentrantLock lock = new ReentrantLock(true);

    public record MatchResult(boolean matched, String reason, Session session) {}

    public MatchingService(
            AgentRepository agentRepository,
            QueueRepository queueRepository,
            SessionRepository sessionRepository,
            SessionIdGenerator idGen
    ) {
        this.agentRepository = agentRepository;
        this.queueRepository = queueRepository;
        this.sessionRepository = sessionRepository;
        this.idGen = idGen;
    }

    public MatchResult matchNext() {
        lock.lock();
        try {
            Optional<Agent> agentOpt = agentRepository.findFirstAvailable();
            if (agentOpt.isEmpty()) return new MatchResult(false, "NO_AGENT", null);

            Optional<QueueTicket> ticketOpt = queueRepository.dequeueOldest();
            if (ticketOpt.isEmpty()) return new MatchResult(false, "NO_CUSTOMER", null);

            Agent agent = agentOpt.get();
            QueueTicket ticket = ticketOpt.get();

            // 상담(통화)마다 새로운 sessionId 발급
            String sessionId = idGen.generate(10);
            Instant createdAt = Instant.now();
            Instant expiresAt = createdAt.plus(30, ChronoUnit.MINUTES);

            Session session = new Session(sessionId, agent.getAgentId(), ticket.getCustomerId(), createdAt, expiresAt);
            sessionRepository.save(session);

            agent.setStatus(AgentStatus.BUSY);
            agentRepository.save(agent);

            return new MatchResult(true, "OK", session);
        } finally {
            lock.unlock();
        }
    }
}