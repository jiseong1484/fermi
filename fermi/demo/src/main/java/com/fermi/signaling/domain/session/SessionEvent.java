package com.fermi.signaling.domain.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "session_events")
@Getter
@Setter
@NoArgsConstructor
public class SessionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String eventType;

    @Lob // For longer text
    private String details;

    public SessionEvent(Session session, String eventType, String details) {
        this.session = session;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = Instant.now();
    }
}
