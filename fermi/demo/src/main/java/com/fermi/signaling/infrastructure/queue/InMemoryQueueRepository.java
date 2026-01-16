package com.fermi.signaling.infrastructure.queue;

import com.fermi.signaling.domain.queue.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class InMemoryQueueRepository implements QueueRepository {
    private final ConcurrentLinkedQueue<QueueTicket> q = new ConcurrentLinkedQueue<>();

    @Override public void enqueue(QueueTicket ticket) { q.add(ticket); }

    @Override public Optional<QueueTicket> dequeueOldest() { return Optional.ofNullable(q.poll()); }

    @Override public int size() { return q.size(); }
}