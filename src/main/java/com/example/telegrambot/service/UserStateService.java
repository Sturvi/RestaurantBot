package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserState;
import com.example.telegrambot.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStateService {
    private final UserStateRepository userStateRepository;

    public String changeUserState(String userStatus, TelegramObject telegramObject) {
        return changeUserState(userStatus, telegramObject.getId());
    }

    public String changeUserState (String userStatus, Long chatId) {
        log.debug("Обновление статуса пользователя с chatId: {}", chatId);
        UserState userState = userStateRepository.findByChatId(chatId).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(chatId);
            return newUserState;
        });

        userState.setUserState(userStatus);
        userStateRepository.save(userState);
        log.debug("Статус пользователя с chatId: {} успешно обновлен на {}", chatId, userStatus);

        return userStatus;
    }

    public String getUserStatus (Long chatId) {
        var userStateOptional = userStateRepository.findByChatId(chatId);

        if (userStateOptional.isPresent()){
            return userStateOptional.get().getUserState();
        } else {
            return changeUserState("main", chatId);
        }
    }
}
