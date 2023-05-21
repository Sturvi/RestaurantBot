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

    /**
     * Changes the user state for the given chat ID to the specified user status.
     *
     * @param userStatus     the new user status
     * @param telegramObject the TelegramObject containing the chat ID
     */
    public void changeUserState(String userStatus, TelegramObject telegramObject) {
        changeUserState(userStatus, telegramObject.getId());
    }

    /**
     * Changes the user state for the given chat ID to the specified user status.
     *
     * @param userStatus the new user status
     * @param chatId the chat ID of the user
     */
    public void changeUserState (String userStatus, Long chatId) {
        log.debug("Updating user state for chat ID: {}", chatId);
        UserState userState = userStateRepository.findByChatId(chatId).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(chatId);
            return newUserState;
        });

        userState.setUserState(userStatus);
        userStateRepository.save(userState);
        log.debug("User state for chat ID: {} successfully updated to {}", chatId, userStatus);
    }

    /**
     * Gets the user status for the given chat ID. If the user does not have a status, sets it to "main".
     *
     * @param chatId the chat ID of the user
     * @return the user status
     */
    public String getUserStatus (Long chatId) {
        var userStateOptional = userStateRepository.findByChatId(chatId);

        if (userStateOptional.isPresent()){
            return userStateOptional.get().getUserState();
        } else {
            changeUserState("main", chatId);
            return "main";
        }
    }
}