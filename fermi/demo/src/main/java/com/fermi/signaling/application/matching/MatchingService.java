package com.fermi.signaling.application.matching;

import com.fermi.signaling.application.agent.AgentService;
import com.fermi.signaling.application.queue.QueueService;
import com.fermi.signaling.application.session.SessionService;
import com.fermi.signaling.domain.agent.Agent;
import com.fermi.signaling.domain.agent.AgentStatus;
import com.fermi.signaling.domain.queue.QueueTicket;
import com.fermi.signaling.domain.session.Session;
import com.fermi.signaling.signaling.SignalingHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MatchingService {

    private final AgentService agentService;
    private final QueueService queueService;
    private final SessionService sessionService;
    private final SignalingHandler signalingHandler;

    public MatchingService(AgentService agentService, QueueService queueService, SessionService sessionService, SignalingHandler signalingHandler) {
        this.agentService = agentService;
        this.queueService = queueService;
        this.sessionService = sessionService;
        this.signalingHandler = signalingHandler;
    }

    private final ReentrantLock lock = new ReentrantLock(true);

    public record MatchResult(boolean matched, String reason, Session session) {}

    public MatchResult matchNext(String agentId) {
        lock.lock();
        try {
            // 1. 상담사 찾기 및 상태 확인
            Optional<Agent> agentOpt = agentService.findById(agentId); // AgentService에 findById 추가 필요
            if (agentOpt.isEmpty()) {
                return new MatchResult(false, "AGENT_NOT_FOUND", null);
            }
            Agent agent = agentOpt.get();

            if (agent.getStatus() != AgentStatus.AVAILABLE) {
                return new MatchResult(false, "AGENT_NOT_AVAILABLE", null);
            }

            // 2. 큐에서 고객 찾기
            Optional<QueueTicket> ticketOpt = queueService.dequeueOldest();
            if (ticketOpt.isEmpty()) {
                return new MatchResult(false, "NO_CUSTOMER_IN_QUEUE", null);
            }
            QueueTicket ticket = ticketOpt.get();
            String customerId = ticket.getCustomerId();

            // 3. 세션 생성
            Session session = sessionService.createSession(agentId, customerId);

            // 4. 상담사 상태 BUSY로 변경
            agentService.setBusy(agentId);

            // 5. 고객과 상담사에게 WebSocket 메시지 전송
            Map<String, Object> message = Map.of(
                    "type", "session-matched",
                    "sessionId", session.getSessionId(),
                    "agentId", agentId,
                    "customerId", customerId
            );

            try {
                signalingHandler.sendMessageToUser(agentId, message);
                signalingHandler.sendMessageToUser(customerId, message);
            } catch (IOException e) {
                // 메시지 전송 실패 시 처리 (로그 기록 등)
                System.err.println("Failed to send session-matched message: " + e.getMessage());
                // TODO: 롤백 처리 (세션 삭제, 상담사 상태 AVAILABLE로 복원, 고객 큐에 다시 넣기)
                return new MatchResult(false, "WEBSOCKET_SEND_FAILED", null);
            }

            return new MatchResult(true, "OK", session);
        } finally {
            lock.unlock();
        }
    }
}
