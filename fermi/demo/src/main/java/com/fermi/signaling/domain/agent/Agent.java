package com.fermi.signaling.domain.agent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
public class Agent {

    @Id
    private String agentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status;

    @Column(nullable = false)
    private Instant updatedAt;

    public Agent(String agentId, AgentStatus status){
        this.agentId = agentId;
        this.status = status;
        this.updatedAt = Instant.now();
    }
    
    public void setStatus(AgentStatus status){
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
