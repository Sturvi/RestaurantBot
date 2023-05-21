package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.ReviewEntity;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserPhoneNumberEntity;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.repository.UserPhoneNumberRepository;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.service.UserStateService;
import com.example.telegrambot.service.messagesenders.AdminMessageSender;
import com.example.telegrambot.service.ReviewService;
import com.example.telegrambot.service.messagesenders.UserMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Component responsible for handling message updates in a Telegram Bot context.
 * This component is annotated as a prototype scoped bean, meaning a new instance is created every time it is needed.
 * It implements the Handler interface.
 */
@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class MessageUpdateHandler implements Handler {
    private TelegramObject telegramObject;
    private final UserStateService userStateService;
    private final UserPhoneNumberRepository userPhoneNumberRepository;
    private final AdminMessageSender adminMessageSender;
    private final UserRepository userRepository;
    private final ReviewService reviewService;
    private final UserMessageSender userMessageSender;

    /**
     * Main method to handle incoming Telegram objects.
     *
     * @param telegramObject The incoming Telegram object to be handled.
     */
    @Override
    public void handle(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        userMessageSender.setTelegramObject(telegramObject);

        log.debug("Handling update for message with text: {}, chat ID: {}", telegramObject.getText(), telegramObject.getId());

        UserStateEnum userState = userStateService.getUserStatus(telegramObject.getId());


        if (Boolean.TRUE.equals(telegramObject.isContact()) && userState == UserStateEnum.REQUEST_PHONE_NUMBER) {
            handlingContact();
        } else {
            handlingTextMessage();
        }

    }

    /**
     * Handles the situation when the user sends a contact update.
     */
    private void handlingContact() {
        log.debug("Handling contact with phone number: {}, chat ID: {}", telegramObject.getPhoneNumber(), telegramObject.getId());

        UserPhoneNumberEntity newUserPhoneNumberEntity = userPhoneNumberRepository.findByChatId(telegramObject.getId())
                .map(existingUserPhoneNumberEntity -> {
                    existingUserPhoneNumberEntity.setPhoneNumber(telegramObject.getPhoneNumber());
                    return existingUserPhoneNumberEntity;
                })
                .orElseGet(() -> UserPhoneNumberEntity
                        .builder()
                        .chatId(telegramObject.getId())
                        .phoneNumber(telegramObject.getPhoneNumber())
                        .build());

        userPhoneNumberRepository.save(newUserPhoneNumberEntity);

        userStateService.changeUserState(UserStateEnum.MESSAGE_TO_ADMIN, telegramObject);

        userMessageSender.sendMessage("Теперь можете прислать ваше сообщение.");
    }

    /**
     * Handles incoming text messages, performs corresponding actions based on the message text.
     */
    private void handlingTextMessage() {
        log.debug("Handling text message");

        switch (telegramObject.getText()) {
            case ("/start") -> {
                userStateService.changeUserState(UserStateEnum.MAIN, telegramObject);
                userMessageSender.sendMessage("Добро пожаловать в наш бот");
                log.debug("User started the bot");
            }
            case ("\uD83D\uDCDD Оставить отзыв") -> {
                userStateService.changeUserState(UserStateEnum.REVIEW, telegramObject);
                userMessageSender.sendMessage("Пришлите ваш отзыв в виде сообщения");
                log.debug("User requested to leave a review");
            }
            case ("⛔ Отмена") -> {
                userStateService.changeUserState(UserStateEnum.MAIN, telegramObject);
                userMessageSender.sendMessage("Вернулись в главное меню");
                log.debug("User requested to leave a main");
            }
            case ("\uD83D\uDCAC Написать администратору") -> {
                userStateService.changeUserState(UserStateEnum.MESSAGE_TO_ADMIN, telegramObject);
                messageForAdmin();
            }
            default -> {
                UserStateEnum userState = userStateService.getUserStatus(telegramObject.getId());

                switch (userState) {
                    case REVIEW -> addReview();
                    case REQUEST_PHONE_NUMBER -> userMessageSender.sendMessage("Для того, чтобы отправить сообщение администратору " +
                            "пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.");
                    case MESSAGE_TO_ADMIN -> {

                    }
                }
            }
        }
    }

    /**
     * Prepares a message for the admin based on the user's current state.
     */
    private void messageForAdmin() {
        String messageText = "Здесь Вы можете написать сообщение Управляющему! " +
                "Это может быть благодарность, отзыв, предложение, замечание, претензия и другое.";

        Optional<UserPhoneNumberEntity> userPhoneNumber = userPhoneNumberRepository.findByChatId(telegramObject.getId());

        if (userPhoneNumber.isEmpty()) {
            userStateService.changeUserState(UserStateEnum.REQUEST_PHONE_NUMBER, telegramObject);

            messageText += "\n\nНо для начала пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.";
        }

        userMessageSender.sendMessage(messageText);

    }

    /**
     * Creates a review based on the user's text message and sends it to the admin.
     */
    private void addReview() {
        Optional<UserEntity> userInDataBase = userRepository.findByChatId(telegramObject.getId());

        if (userInDataBase.isPresent()) {
            ReviewEntity reviewEntity = reviewService.createReview(userInDataBase.get(), telegramObject.getText());

            userStateService.changeUserState(UserStateEnum.MAIN, telegramObject);
            userMessageSender.sendMessage("Спасибо за ваш отзыв");

            adminMessageSender.sendMessageToAllAdmin(telegramObject.getText(), telegramObject);

            log.debug("Review added: {}", reviewEntity);
        } else {
            log.error("Failed to add review because user with id {} account was not found in the database", telegramObject.getId());
            userMessageSender.sendMessage("Произошла ошибка во время отправки отзыва. Пожалуйста, попробуйте позже.");
        }
    }
}
