package com.example.telegrambot;

import com.example.telegrambot.model.Review;
import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.model.UserPhoneNumber;
import com.example.telegrambot.model.UserState;
import com.example.telegrambot.repository.AllRepository;

import com.example.telegrambot.service.AdminMessageSender;
import com.example.telegrambot.service.UserMessageSender;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Обработчик обновлений.
 */
@Component
@Scope("prototype")
public class UpdateHandler {

    @Autowired
    AllRepository allRepository;

    @Autowired
    private UserMessageSender userMessageSender;

    @Autowired
    AdminMessageSender adminMessageSender;
    private TelegramObject telegramObject;
    private static final Logger LOGGER = Logger.getLogger(UpdateHandler.class);


    public void setTelegramObject(Update update) {
        if (isMessageWithText(update)) {
            this.telegramObject = new TelegramObject(update.getMessage());
        } else if (isCallbackWithData(update)) {
            this.telegramObject = new TelegramObject(update.getCallbackQuery());
        }
        userMessageSender.setTelegramObject(telegramObject);
    }

    /**
     * Обрабатывает обновление бота.
     * Если обновление является сообщением с текстом, вызывает метод updateUserInfo() для обновления информации о пользователе.
     * Если обновление является обратным вызовом с данными, вызывает метод updateUserInfo() для обновления информации о пользователе.
     */
    public void handling() {
        try {
            if (telegramObject.isMessage()) {
                LOGGER.info(String.format("Handling update for message with text: %s, chat ID: %d", telegramObject.getText(), telegramObject.getId()));
                updateUserInfo(telegramObject.getFrom());
                handlingTextMessage();
            } else if (telegramObject.isCallbackQuery()) {
                LOGGER.info(String.format("Handling update for callback with data: %s, chat ID: %d", telegramObject.getData(), telegramObject.getId()));
                updateUserInfo(telegramObject.getFrom());
            }
        } catch (Exception e){
            LOGGER.error("An error occurred while handling the Telegram object", e);
        }

    }

    /**
     * Обрабатывает текстовые сообщения от пользователя и выполняет соответствующие действия.
     */
    private void handlingTextMessage() {
        LOGGER.info("Handling text message");

        switch (telegramObject.getText()) {
            case ("/start") -> {
                changeUserState("main");
                userMessageSender.sendMessage("Добро пожаловать в наш бот");
                LOGGER.info("User started the bot");
            }
            case ("\uD83D\uDCDD Оставить отзыв") -> {
                changeUserState("review");
                userMessageSender.sendMessage("Пришлите ваш отзыв в виде сообщения");
                LOGGER.info("User requested to leave a review");
            }
            case ("⛔ Отмена") -> {
                changeUserState("main");
                userMessageSender.sendMessage("Вернулись в главное меню");
                LOGGER.info("User requested to leave a main");
            }
            case ("\uD83D\uDCAC Написать администратору") -> {
                changeUserState("messageToAdmin");
                messageForAdmin();
            }
            default -> {
                String userStatus = allRepository.getUserStateRepository().findById(telegramObject.getId()).get().getUserState();

                switch (userStatus) {
                    case ("review") -> {
                        addReview();
                        LOGGER.info("User left a review");
                    }
                }
            }
        }
    }

    private void messageForAdmin () {
        String messageText = "Здесь Вы можете написать сообщение Управляющему! " +
                "Это может быть благодарность, отзыв, предложение, замечание, претензия и другое.";

        Optional<UserPhoneNumber> userPhoneNumber = allRepository
                .getUserPhoneNumberRepository()
                .findById(telegramObject.getId());

        if (userPhoneNumber.isEmpty()) {
            changeUserState("messageToAdminNONUMBER");

            messageText += "\n\n" +
                    "Но для начала пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.";
        }

        userMessageSender.sendMessage(messageText);
    }

    private void addReview() {
        Optional<UserInDataBase> userInDataBase = allRepository.getUserRepository().findById(telegramObject.getId());

        Review review = Review.builder()
                .user(userInDataBase.get())
                .message(telegramObject.getText())
                .build();

        allRepository.getReviewRepository().save(review);

        changeUserState("main");
        userMessageSender.sendMessage("Спасибо за ваш отзыв");

        adminMessageSender.sendMessageToAllAdmin(telegramObject.getText(), telegramObject);

        LOGGER.info(String.format("Review added: %s", review));

    }

    /**
     * Проверяет, содержит ли обновление сообщение с текстом.
     *
     * @return true, если обновление содержит сообщение с текстом, иначе false.
     */
    private static boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    /**
     * Проверяет, содержит ли обновление обратный вызов с данными.
     *
     * @return true, если обновление содержит обратный вызов с данными, иначе false.
     */
    private static boolean isCallbackWithData(Update update) {
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

        return allRepository.getUserRepository().findById(user.getId()).map(existingUser -> {
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

        allRepository.getUserRepository().save(userInDataBase);

        return userInDataBase;
    }

    private void changeUserState(String userStatus) {
        LOGGER.info(String.format("Обновление статуса пользователя с chatId: %d", telegramObject.getId()));
        UserState userState = allRepository.getUserStateRepository().findById(telegramObject.getId()).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(telegramObject.getId());
            return newUserState;
        });

        userState.setUserStatus(userStatus);
        allRepository.getUserStateRepository().save(userState);
        LOGGER.info(String.format("Статус пользователя с chatId: %d успешно обновлен на %s", telegramObject.getId(), userStatus));
    }
}
