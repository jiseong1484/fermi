
package com.fermi.signaling.api.queue;

import com.fermi.signaling.application.queue.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@RequestBody JoinQueueRequest request) {
        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            return ResponseEntity.badRequest().body("Customer ID is required.");
        }

        try {
            String ticketId = queueService.enqueue(request.getCustomerId());
            return ResponseEntity.ok(Map.of("status", "success", "ticketId", ticketId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/size")
    public ResponseEntity<?> getQueueSize() {
        try {
            int size = queueService.size();
            return ResponseEntity.ok(Map.of("size", size));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
