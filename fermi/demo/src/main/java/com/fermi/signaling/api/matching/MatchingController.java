
package com.fermi.signaling.api.matching;

import com.fermi.signaling.application.matching.MatchingService;
import com.fermi.signaling.domain.session.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/next")
    public ResponseEntity<?> matchNext(@RequestBody MatchNextRequest request) {
        if (request.getAgentId() == null || request.getAgentId().isBlank()) {
            return ResponseEntity.badRequest().body("Agent ID is required.");
        }

        try {
            MatchingService.MatchResult result = matchingService.matchNext(request.getAgentId());

            if (result.matched()) {
                Session session = result.session();
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("status", "success");
                responseBody.put("message", "Matched successfully");
                responseBody.put("sessionId", session.getSessionId());
                responseBody.put("agentId", session.getAgentId());
                responseBody.put("customerId", session.getCustomerId());
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "reason", result.reason()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
