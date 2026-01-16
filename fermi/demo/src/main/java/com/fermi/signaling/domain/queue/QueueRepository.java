package com.fermi.signaling.domain.queue;

import java.util.Optional;

public interface QueueRepository {
    void enqueue(QueueTicket ticket);
    Optional<QueueTicket> dequeueOldest();
    int size();
}