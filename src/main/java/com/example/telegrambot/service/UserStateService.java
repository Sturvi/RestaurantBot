package com.example.telegrambot.service;

import com.example.telegrambot.model.UserState;
import com.example.telegrambot.repository.UserStateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserStateService {

    @Autowired
    private UserStateRepository userStateRepository;

    @Transactional
    public void updateUserStatus(Long chatId, String userStatus) {
        UserState userState = userStateRepository.findById(chatId).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(chatId);
            return newUserState;
        });

        userState.setUserStatus(userStatus);
        userStateRepository.save(userState);
    }
}
