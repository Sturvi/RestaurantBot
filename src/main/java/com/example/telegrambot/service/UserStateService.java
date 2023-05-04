package com.example.telegrambot.service;

import com.example.telegrambot.model.UserState;
import com.example.telegrambot.repository.UserStateRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервис для работы с состоянием пользователя.
 */
public class UserStateService {

    private UserStateRepository userStateRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserStateService.class);

    /**
     * Конструктор для инициализации UserStateService с UserStateRepository.
     *
     * @param userStateRepository репозиторий для работы с состоянием пользователя
     */
    public UserStateService(UserStateRepository userStateRepository) {
        this.userStateRepository = userStateRepository;
    }

    /**
     * Обновляет статус пользователя с указанным chatId.
     *
     * @param chatId идентификатор пользователя
     * @param userStatus новый статус пользователя
     */
    @Transactional
    public void updateUserStatus(Long chatId, String userStatus) {
        LOGGER.info(String.format("Обновление статуса пользователя с chatId: %d", chatId));
        UserState userState = userStateRepository.findById(chatId).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(chatId);
            return newUserState;
        });

        userState.setUserStatus(userStatus);
        userStateRepository.save(userState);
        LOGGER.info(String.format("Статус пользователя с chatId: %d успешно обновлен на %s", chatId, userStatus));
    }

    /**
     * Возвращает статус пользователя с указанным chatId.
     *
     * @param chatId идентификатор пользователя
     * @return статус пользователя или null, если пользователь не найден
     */
    public String getUserStatus(Long chatId) {
        LOGGER.info(String.format("Получение статуса пользователя с chatId: %d", chatId));
        String userStatus = userStateRepository.findById(chatId)
                .map(UserState::getUserState)
                .orElse(null);

        if (userStatus == null) {
            LOGGER.warn(String.format("Пользователь с chatId: %d не найден", chatId));
        } else {
            LOGGER.info(String.format("Получен статус пользователя с chatId: %d - %s", chatId, userStatus));
        }

        return userStatus;
    }
}
