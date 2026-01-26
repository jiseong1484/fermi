package com.fermi.signaling.application.agent;

import com.fermi.signaling.domain.agent.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AgentService {
    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public void registerIfAbsent(String agentId) {
        agentRepository.findById(agentId).orElseGet(() -> {
            Agent a = new Agent(agentId, AgentStatus.OFFLINE);
            agentRepository.save(a);
            return a;
        });
    }

    public void setAvailable(String agentId) {
        registerIfAbsent(agentId);
        Agent a = agentRepository.findById(agentId).orElseThrow();
        a.setStatus(AgentStatus.AVAILABLE);
        agentRepository.save(a);
    }

    public void setBusy(String agentId) {
        registerIfAbsent(agentId);
        Agent a = agentRepository.findById(agentId).orElseThrow();
        a.setStatus(AgentStatus.BUSY);
        agentRepository.save(a);
    }

    public Optional<Agent> findById(String agentId) {
        return agentRepository.findById(agentId);
    }

    public Optional<Agent> findFirstAvailableAgent() {
        return agentRepository.findFirstByStatus(AgentStatus.AVAILABLE);
    }
}