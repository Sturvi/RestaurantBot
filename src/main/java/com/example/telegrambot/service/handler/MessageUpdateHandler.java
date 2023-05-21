package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.Review;
import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.model.UserPhoneNumber;
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
    private UserMessageSender userMessageSender;
    private final AdminMessageSender adminMessageSender;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    /**
     * Main method to handle incoming Telegram objects.
     *
     * @param telegramObject The incoming Telegram object to be handled.
     */
    @Override
    public void handle(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        userMessageSender = new UserMessageSender(telegramObject, userStateService);

        log.debug("Handling update for message with text: {}, chat ID: {}", telegramObject.getText(), telegramObject.getId());

        String userStatus = userStateService.getUserStatus(telegramObject.getId());

        if (Boolean.TRUE.equals(telegramObject.isContact()) && userStatus.equals("messageToAdminNONUMBER")) {
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

        UserPhoneNumber newUserPhoneNumber = userPhoneNumberRepository.findByChatId(telegramObject.getId())
                .map(existingUserPhoneNumber -> {
                    existingUserPhoneNumber.setPhoneNumber(telegramObject.getPhoneNumber());
                    return existingUserPhoneNumber;
                })
                .orElseGet(() -> UserPhoneNumber
                        .builder()
                        .chatId(telegramObject.getId())
                        .phoneNumber(telegramObject.getPhoneNumber())
                        .build());

        userPhoneNumberRepository.save(newUserPhoneNumber);

        userStateService.changeUserState("messageToAdmin", telegramObject);

        userMessageSender.sendMessage("Теперь можете прислать ваше сообщение.");
    }

    /**
     * Handles incoming text messages, performs corresponding actions based on the message text.
     */
    private void handlingTextMessage() {
        log.debug("Handling text message");

        switch (telegramObject.getText()) {
            case ("/start") -> {
                userStateService.changeUserState("main", telegramObject);
                userMessageSender.sendMessage("Добро пожаловать в наш бот");
                log.debug("User started the bot");
            }
            case ("\uD83D\uDCDD Оставить отзыв") -> {
                userStateService.changeUserState("review", telegramObject);
                userMessageSender.sendMessage("Пришлите ваш отзыв в виде сообщения");
                log.debug("User requested to leave a review");
            }
            case ("⛔ Отмена") -> {
                userStateService.changeUserState("main", telegramObject);
                userMessageSender.sendMessage("Вернулись в главное меню");
                log.debug("User requested to leave a main");
            }
            case ("\uD83D\uDCAC Написать администратору") -> {
                userStateService.changeUserState("messageToAdmin", telegramObject);
                messageForAdmin();
            }
            default -> {
                String userStatus = userStateService.getUserStatus(telegramObject.getId());

                switch (userStatus) {
                    case ("review") -> {
                        addReview();
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

        Optional<UserPhoneNumber> userPhoneNumber = userPhoneNumberRepository.findByChatId(telegramObject.getId());

        if (userPhoneNumber.isEmpty()) {
            userStateService.changeUserState("messageToAdminNONUMBER", telegramObject);

            messageText += "\n\nНо для начала пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.";
        }

        userMessageSender.sendMessage(messageText);

    }

    /**
     * Creates a review based on the user's text message and sends it to the admin.
     */
    private void addReview() {
        Optional<UserInDataBase> userInDataBase = userRepository.findByChatId(telegramObject.getId());

        if (userInDataBase.isPresent()) {
            Review review = reviewService.createReview(userInDataBase.get(), telegramObject.getText());

            userStateService.changeUserState("main", telegramObject);
            userMessageSender.sendMessage("Спасибо за ваш отзыв");

            adminMessageSender.sendMessageToAllAdmin(telegramObject.getText(), telegramObject);

            log.debug("Review added: {}", review);
        } else {
            log.error("Failed to add review because user with id {} account was not found in the database", telegramObject.getId());
            userMessageSender.sendMessage("Произошла ошибка во время отправки отзыва. Пожалуйста, попробуйте позже.");
        }
    }
}
