package com.fermi.signaling.domain.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "session_summaries")
@Getter
@Setter
@NoArgsConstructor
public class SessionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private Session session;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public SessionSummary(Session session, String content) {
        this.session = session;
        this.content = content;
        this.createdAt = Instant.now();
    }
}
