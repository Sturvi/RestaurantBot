package com.example.telegrambot.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllRepository {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStateRepository userStateRepository;

    @Autowired
    ReviewRepository reviewRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UserStateRepository getUserStateRepository() {
        return userStateRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }
}
