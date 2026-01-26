package com.fermi.signaling.domain.agent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {
    Optional<Agent> findFirstByStatus(AgentStatus status);
}
