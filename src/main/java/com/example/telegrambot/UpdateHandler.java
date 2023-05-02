package com.example.telegrambot;

import com.example.telegrambot.model.Review;
import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateHandler {

    UserRepository userRepository;
    private TelegramObject telegramObject;
    private static final Logger LOGGER = Logger.getLogger(UpdateHandler.class);

    private UpdateHandler(UserRepository userRepository, TelegramObject telegramObject) {
        this.userRepository = userRepository;
        this.telegramObject = telegramObject;
    }

    public UpdateHandler getUpdateHandler(Update update, UserRepository userRepository) {
        if (isMessageWithText(update)) {
            TelegramObject newTelegramObject = new TelegramObject(update.getMessage());
            return new UpdateHandler(userRepository, newTelegramObject);
        } else if (isCallbackWithData(update)) {
            TelegramObject newTelegramObject = new TelegramObject(update.getCallbackQuery());
            return new UpdateHandler(userRepository, newTelegramObject);
        } else return null;
    }

    /**
     * Обрабатывает обновление бота.
     * Если обновление является сообщением с текстом, вызывает метод updateUserInfo() для обновления информации о пользователе.
     * Если обновление является обратным вызовом с данными, вызывает метод updateUserInfo() для обновления информации о пользователе.
     */
    public void handling() {
        if (telegramObject.isMessage()) {
            LOGGER.info(String.format("Handling update for message with text: %s, chat ID: %d", telegramObject.getText(), telegramObject.getId()));
            updateUserInfo(telegramObject.getFrom());
            handlingTextMessage();
        } else if (telegramObject.isCallbackQuery()) {
            LOGGER.info(String.format("Handling update for callback with data: %s, chat ID: %d", telegramObject.getData(), telegramObject.getId()));
            UserInDataBase userInDataBase = updateUserInfo(telegramObject.getFrom());
        }
    }

    private void handlingTextMessage() {
        switch (telegramObject.getText()) {
            case ("Оставить отзыв") -> {
                changeUserState("review");
                //Отправить пользователю сообщение
            }
            default -> {
                //если State == review то вызвать addReview()
            }
        }
    }

    private Review addReview() {
        return null;
    }

    /**
     * Проверяет, содержит ли обновление сообщение с текстом.
     *
     * @return true, если обновление содержит сообщение с текстом, иначе false.
     */
    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    /**
     * Проверяет, содержит ли обновление обратный вызов с данными.
     *
     * @return true, если обновление содержит обратный вызов с данными, иначе false.
     */
    private boolean isCallbackWithData(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty();
    }

    /**
     * Обновляет информацию о пользователе в базе данных.
     * Если пользователь уже существует в базе данных и с последнего обновления прошло больше суток,
     * то обновляет информацию о пользователе.
     * Если пользователь не существует в базе данных, добавляет его.
     *
     * @param user Объект User, содержащий информацию о пользователе.
     * @return Объект UserInDataBase, содержащий обновленную информацию о пользователе.
     */
    private UserInDataBase updateUserInfo(User user) {
        LOGGER.info(String.format("Updating user info for user with ID: %d", user.getId()));

        return userRepository.findById(user.getId()).map(existingUser -> {
            Duration duration = Duration.between(existingUser.getUpdatedAt(), LocalDateTime.now());
            if (duration.toDays() >= 1) {
                LOGGER.info(String.format("User with ID: %d was last updated more than a day ago, updating...", user.getId()));
                return updateUserInDataBase(user);
            } else {
                LOGGER.info(String.format("User with ID: %d was last updated less than a day ago, no update needed", user.getId()));
                return existingUser;
            }
        }).orElseGet(() -> {
            LOGGER.info(String.format("User with ID: %d not found in database, creating new user...", user.getId()));
            return updateUserInDataBase(user);
        });
    }

    /**
     * Сохраняет или обновляет информацию о пользователе в базе данных.
     *
     * @param user Объект User, содержащий информацию о пользователе.
     * @return Объект UserInDataBase, содержащий обновленную информацию о пользователе.
     */
    private UserInDataBase updateUserInDataBase(User user) {
        LOGGER.info(String.format("Saving user with ID: %d", user.getId()));

        UserInDataBase userInDataBase = UserInDataBase.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .userStatus(true)
                .build();

        userRepository.save(userInDataBase);

        return userInDataBase;
    }

    private void changeUserState(String userState) {

    }
}
