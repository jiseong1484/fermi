package com.fermi.signaling.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionSummaryRepository extends JpaRepository<SessionSummary, Long> {
}
