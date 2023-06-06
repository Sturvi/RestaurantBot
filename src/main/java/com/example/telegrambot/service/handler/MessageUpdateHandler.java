package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserPhoneNumberEntity;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.repository.UserPhoneNumberRepository;
import com.example.telegrambot.service.UserService;
import com.example.telegrambot.service.handler.eventhandlers.ChatEventHandler;
import com.example.telegrambot.service.handler.eventhandlers.CustomerEventHandler;
import com.example.telegrambot.service.messages.messagesenders.UserMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

    private final UserService userService;
    private final UserPhoneNumberRepository userPhoneNumberRepository;
    private final UserMessageSender userMessageSender;
    private final CustomerEventHandler customerEventHandler;
    private final ChatEventHandler chatEventHandler;

    private final Map<String, Consumer<TelegramObject>> messageHandlers = new HashMap<>();

    private void init(TelegramObject telegramObject) {
        messageHandlers.put("/start", this::handleStart);
        messageHandlers.put("\uD83D\uDCDD Оставить отзыв", this::handleReview);
        messageHandlers.put("⛔ Отмена", this::handleCancel);
        messageHandlers.put("\uD83D\uDCAC Написать администратору", this::handleMessageToAdmin);
        userMessageSender.setTelegramObject(telegramObject);
    }

    /**
     * Main method to handle incoming Telegram objects.
     *
     * @param telegramObject The incoming Telegram object to be handled.
     */
    @Override
    public void handle(TelegramObject telegramObject) {
        init(telegramObject);

        log.debug("Handling update for message with text: {}, chat ID: {}", telegramObject.getText(), telegramObject.getId());

        UserStateEnum userState = userService.getUserState(telegramObject);

        if (Boolean.TRUE.equals(telegramObject.isContact()) && userState == UserStateEnum.REQUEST_PHONE_NUMBER) {
            handlingContact(telegramObject);
        } else {
            handlingTextMessage(telegramObject);
        }
    }

    /**
     * Handles the situation when the user sends a contact update.
     */
    private void handlingContact(TelegramObject telegramObject) {
        log.debug("Handling contact with phone number: {}, chat ID: {}", telegramObject.getPhoneNumber(), telegramObject.getId());

        UserPhoneNumberEntity newUserPhoneNumberEntity = mapToUserPhoneNumberEntity(telegramObject);

        userPhoneNumberRepository.save(newUserPhoneNumberEntity);

        userService.changeUserState(UserStateEnum.MESSAGE_TO_ADMIN, telegramObject);

        userMessageSender.sendMessage("Теперь можете прислать ваше сообщение.");
    }


    private UserPhoneNumberEntity mapToUserPhoneNumberEntity(TelegramObject telegramObject) {
        return userPhoneNumberRepository.findByChatId(telegramObject.getId())
                .map(existingUserPhoneNumberEntity -> {
                    existingUserPhoneNumberEntity.setPhoneNumber(telegramObject.getPhoneNumber());
                    return existingUserPhoneNumberEntity;
                })
                .orElseGet(() -> UserPhoneNumberEntity
                        .builder()
                        .chatId(telegramObject.getId())
                        .phoneNumber(telegramObject.getPhoneNumber())
                        .build());
    }

    /**
     * Handles incoming text messages, performs corresponding actions based on the message text.
     */
    private void handlingTextMessage(TelegramObject telegramObject) {
        log.debug("Handling text message");

        Consumer<TelegramObject> handler = messageHandlers.getOrDefault(telegramObject.getText(), this::handleDefault);
        handler.accept(telegramObject);
    }

    private void handleStart(TelegramObject telegramObject) {
        userService.changeUserState(UserStateEnum.MAIN, telegramObject);
        userMessageSender.sendMessage("Добро пожаловать в наш бот");
        log.debug("User started the bot");
    }

    private void handleReview(TelegramObject telegramObject) {
        userService.changeUserState(UserStateEnum.REVIEW, telegramObject);
        userMessageSender.sendMessage("Пришлите ваш отзыв в виде сообщения");
        log.debug("User requested to leave a review");
    }

    private void handleCancel(TelegramObject telegramObject) {
        userService.changeUserState(UserStateEnum.MAIN, telegramObject);
        userMessageSender.sendMessage("Вернулись в главное меню");
        log.debug("User requested to leave a main");
    }

    private void handleMessageToAdmin(TelegramObject telegramObject) {
        userService.changeUserState(UserStateEnum.MESSAGE_TO_ADMIN, telegramObject);
        messageForAdmin(telegramObject);
    }

    private void handleDefault(TelegramObject telegramObject) {
        UserStateEnum userState = userService.getUserState(telegramObject);

        switch (userState) {
            case REVIEW -> customerEventHandler.handleNewCustomerReview(telegramObject);
            case REQUEST_PHONE_NUMBER ->
                    userMessageSender.sendMessage("Для того, чтобы отправить сообщение администратору " +
                            "пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.");
            case MESSAGE_TO_ADMIN, ANSWER_TO_MESSAGE_IN_CHAT -> {
                chatEventHandler.handle(telegramObject);
                userService.changeUserState(UserStateEnum.MAIN, telegramObject);
            }
            default ->
                    userMessageSender.sendMessage("Что-то пошло не так, пожалуйста сообщите об ошибке аккаунту @Sturvi");

        }
    }

    /**
     * Prepares a message for the admin based on the user's current state.
     */
    private void messageForAdmin(TelegramObject telegramObject) {
        String messageText = "Здесь Вы можете написать сообщение Управляющему! " +
                "Это может быть благодарность, отзыв, предложение, замечание, претензия и другое.";

        Optional<UserPhoneNumberEntity> userPhoneNumber = userPhoneNumberRepository.findByChatId(telegramObject.getId());

        if (userPhoneNumber.isEmpty()) {
            userService.changeUserState(UserStateEnum.REQUEST_PHONE_NUMBER, telegramObject);

            messageText += "\n\nНо для начала пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.";
        }

        userMessageSender.sendMessage(messageText);

    }
}