package com.example.telegrambot.service;

import com.example.telegrambot.model.Review;
import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Creates a new review for a user.
     *
     * @param user    the user for whom the review is being created
     * @param message the message of the review
     * @return the saved review
     */
    public Review createReview(UserInDataBase user, String message) {
        log.debug("Creating new review from user: {}", user);
        Review review = Review.builder()
                .user(user)
                .message(message)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.debug("Review successfully saved: {}", savedReview);

        return savedReview;
    }
}