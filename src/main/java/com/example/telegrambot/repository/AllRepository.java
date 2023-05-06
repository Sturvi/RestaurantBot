package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserPhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllRepository {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStateRepository userStateRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private UserPhoneNumberRepository userPhoneNumberRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UserStateRepository getUserStateRepository() {
        return userStateRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }

    public UserRolesRepository getUserRolesRepository() {
        return userRolesRepository;
    }

    public UserPhoneNumberRepository getUserPhoneNumberRepository() {
        return userPhoneNumberRepository;
    }
}
