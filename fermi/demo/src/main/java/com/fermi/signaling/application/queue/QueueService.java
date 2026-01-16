package com.fermi.signaling.application.queue;

import com.fermi.signaling.common.util.SessionIdGenerator;
import com.fermi.signaling.domain.queue.QueueRepository;
import com.fermi.signaling.domain.queue.QueueTicket;

public class QueueService {
    private final QueueRepository queueRepository;
    private final SessionIdGenerator idGen;

    public QueueService(QueueRepository queueRepository, SessionIdGenerator idGen) {
        this.queueRepository = queueRepository;
        this.idGen = idGen;
    }

    public String enqueue(String customerId) {
        String ticketId = idGen.generate(10);
        queueRepository.enqueue(new QueueTicket(ticketId, customerId));
        return ticketId;
    }

    public int size() {
        return queueRepository.size();
    }
}