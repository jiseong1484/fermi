package com.fermi.signaling.application.queue;

import com.fermi.signaling.common.util.SessionIdGenerator;
import com.fermi.signaling.domain.queue.QueueRepository;
import com.fermi.signaling.domain.queue.QueueTicket;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QueueService {
    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public String enqueue(String customerId) {
        String ticketId = SessionIdGenerator.generate(10);
        queueRepository.enqueue(new QueueTicket(ticketId, customerId));
        return ticketId;
    }

    public int size() {
        return queueRepository.size();
    }

    public Optional<QueueTicket> dequeueOldest() {
        return queueRepository.dequeueOldest();
    }
}