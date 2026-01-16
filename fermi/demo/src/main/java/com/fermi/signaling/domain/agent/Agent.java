package com.fermi.signaling.domain.agent;
import java.time.Instant;

public class Agent {
    private final String agentId;
    private AgentStatus status;
    private Instant updatedAt;

    public Agent(String agentId, AgentStatus status){
        this.agentId = agentId;
        this.status = status;
        this.updatedAt = Instant.now();
    }
    public String getAgentId() {
        return agentId;
    }

    public AgentStatus getStatus() {
        return status;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setStatus(AgentStatus status){
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
