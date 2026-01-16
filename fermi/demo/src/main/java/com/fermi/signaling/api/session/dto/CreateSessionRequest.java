package com.fermi.signaling.api.session.dto;

public record CreateSessionRequest(
        String agentId,
        String customerId
) {}