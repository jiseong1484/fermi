
package com.fermi.signaling.api.agent;

import com.fermi.signaling.application.agent.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/{agentId}/available")
    public ResponseEntity<?> setAgentAvailable(@PathVariable String agentId) {
        if (agentId == null || agentId.isBlank()) {
            return ResponseEntity.badRequest().body("Agent ID is required.");
        }
        try {
            agentService.setAvailable(agentId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Agent " + agentId + " set to AVAILABLE"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
