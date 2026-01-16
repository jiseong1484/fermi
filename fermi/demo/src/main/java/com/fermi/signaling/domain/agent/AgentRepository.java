package com.fermi.signaling.domain.agent;

import java.util.Optional;

public interface AgentRepository {
    void save(Agent agent);
    Optional<Agent> findById(String agentId);
    Optional<Agent> findFirstAvailable();
}
