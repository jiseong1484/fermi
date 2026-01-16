package com.fermi.signaling.domain.queue;

import java.time.Instant;

public class QueueTicket {
    private final String ticketId;
    private final String customerId;
    private final Instant createdAt;

    public QueueTicket(String ticketId, String customerId) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.createdAt = Instant.now();
    }

    public String getTicketId() { return ticketId; }
    public String getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
}