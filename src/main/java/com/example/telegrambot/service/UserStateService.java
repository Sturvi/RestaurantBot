package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserStateEntity;
import com.example.telegrambot.model.UserStateEnum;
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
     * @param newUserState   the new user state
     * @param telegramObject the TelegramObject containing the chat ID
     */
    public void changeUserState(UserStateEnum newUserState, TelegramObject telegramObject) {
        changeUserState(newUserState, telegramObject.getId());
    }

    /**
     * Changes the user state for the given chat ID to the specified user status.
     *
     * @param newUserState the new user state
     * @param chatId       the chat ID of the user
     */
    public void changeUserState(UserStateEnum newUserState, Long chatId) {
        log.debug("Updating user state for chat ID: {}", chatId);
        UserStateEntity userStateEntity = userStateRepository.findByChatId(chatId).orElseGet(() -> {
            UserStateEntity newUserStateEntity = new UserStateEntity();
            newUserStateEntity.setChatId(chatId);
            return newUserStateEntity;
        });

        userStateEntity.setUserStateEnum(newUserState);
        userStateRepository.save(userStateEntity);
        log.debug("User state for chat ID: {} successfully updated to {}", chatId, newUserState);
    }

    /**
     * Gets the user state for the given chat ID. If the user does not have a state, sets it to "main".
     *
     * @param chatId the chat ID of the user
     * @return the user state
     */
    public UserStateEnum getUserStatus(Long chatId) {
        var userStateOptional = userStateRepository.findByChatId(chatId);

        if (userStateOptional.isPresent()) {
            return userStateOptional.get().getUserStateEnum();
        } else {
            changeUserState(UserStateEnum.MAIN, chatId);
            return UserStateEnum.MAIN;
        }
    }
}