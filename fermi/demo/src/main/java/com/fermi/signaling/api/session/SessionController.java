package com.fermi.signaling.api.session;

import com.fermi.signaling.api.session.dto.CreateSessionRequest;
import com.fermi.signaling.api.session.dto.CreateSessionResponse;
import com.fermi.signaling.api.session.dto.GetSessionResponse;
import com.fermi.signaling.application.session.SessionService;
import com.fermi.signaling.domain.session.Session;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public CreateSessionResponse create(@RequestBody CreateSessionRequest req) {
        Session s = sessionService.createSession(req.agentId(), req.customerId());

        String base = sessionService.getFrontendBaseUrl();
        String agentUrl = base + "/agent.html?sessionId=" + s.getSessionId();
        String customerUrl = base + "/join.html?sessionId=" + s.getSessionId();

        return new CreateSessionResponse(
                s.getSessionId(),
                agentUrl,
                customerUrl,
                s.getExpiresAt()
        );
    }

    @GetMapping("/{sessionId}")
    public GetSessionResponse get(@PathVariable String sessionId) {
        Session s = sessionService.getSessionOrThrow(sessionId);
        return new GetSessionResponse(
                s.getSessionId(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getExpiresAt()
        );
    }
}