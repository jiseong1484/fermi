package com.fermi.signaling.application.feedback;

import com.fermi.signaling.api.feedback.dto.CreateFeedbackRequest;
import com.fermi.signaling.domain.feedback.Feedback;
import com.fermi.signaling.domain.feedback.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public void saveFeedback(CreateFeedbackRequest request) {
        if (request == null) {
            return;
        }
        Feedback feedback = new Feedback(
            request.sessionId(),
            request.rating(),
            request.comment()
        );
        feedbackRepository.save(feedback);
    }
}
