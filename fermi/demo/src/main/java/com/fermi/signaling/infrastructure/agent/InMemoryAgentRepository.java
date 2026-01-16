package com.fermi.signaling.infrastructure.agent;

import com.fermi.signaling.domain.agent.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAgentRepository implements AgentRepository {
    private final ConcurrentHashMap<String, Agent> map = new ConcurrentHashMap<>();

    @Override public void save(Agent agent) { map.put(agent.getAgentId(), agent); }

    @Override public Optional<Agent> findById(String agentId) { return Optional.ofNullable(map.get(agentId)); }

    @Override
    public Optional<Agent> findFirstAvailable() {
        return map.values().stream()
                .filter(a -> a.getStatus() == AgentStatus.AVAILABLE)
                .findFirst();
    }
}