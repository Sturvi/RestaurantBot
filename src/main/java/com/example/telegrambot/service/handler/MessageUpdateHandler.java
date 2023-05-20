package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.Review;
import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.model.UserPhoneNumber;
import com.example.telegrambot.model.UserState;
import com.example.telegrambot.repository.UserPhoneNumberRepository;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.repository.UserStateRepository;
import com.example.telegrambot.service.messageSenders.AdminMessageSender;
import com.example.telegrambot.service.ReviewService;
import com.example.telegrambot.service.messageSenders.UserMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("prototype")
@Slf4j
public class MessageUpdateHandler implements Handler {
    private TelegramObject telegramObject;
    private final UserStateRepository userStateRepository;
    private final UserPhoneNumberRepository userPhoneNumberRepository;
    private UserMessageSender userMessageSender;
    private final AdminMessageSender adminMessageSender;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    public MessageUpdateHandler(UserStateRepository userStateRepository, UserPhoneNumberRepository userPhoneNumberRepository, AdminMessageSender adminMessageSender, UserRepository userRepository, ReviewService reviewService) {
        this.userStateRepository = userStateRepository;
        this.userPhoneNumberRepository = userPhoneNumberRepository;
        this.adminMessageSender = adminMessageSender;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }


    @Override
    public void handle(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        userMessageSender = new UserMessageSender(telegramObject, userStateRepository);

        log.debug("Handling update for message with text: {}, chat ID: {}", telegramObject.getText(), telegramObject.getId());

        String userStatus = userStateRepository.findByChatId(telegramObject.getId())
                .map(UserState::getUserState)
                .orElseGet(() -> userStateRepository.changeUserState("main", telegramObject));

        if (Boolean.TRUE.equals(telegramObject.isContact()) && userStatus.equals("messageToAdminNONUMBER") ) {
            handlingContact();
        } else {
            handlingTextMessage();
        }
    }

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

        userStateRepository.changeUserState("messageToAdmin", telegramObject);

        userMessageSender.sendMessage("Теперь можете прислать ваше сообщение.");
    }

    /**
     * Обрабатывает текстовые сообщения от пользователя и выполняет соответствующие действия.
     */
    private void handlingTextMessage() {
        log.debug("Handling text message");

        switch (telegramObject.getText()) {
            case ("/start") -> {
                userStateRepository.changeUserState("main", telegramObject);
                userMessageSender.sendMessage("Добро пожаловать в наш бот");
                log.debug("User started the bot");
            }
            case ("\uD83D\uDCDD Оставить отзыв") -> {
                userStateRepository.changeUserState("review", telegramObject);
                userMessageSender.sendMessage("Пришлите ваш отзыв в виде сообщения");
                log.debug("User requested to leave a review");
            }
            case ("⛔ Отмена") -> {
                userStateRepository.changeUserState("main", telegramObject);
                userMessageSender.sendMessage("Вернулись в главное меню");
                log.debug("User requested to leave a main");
            }
            case ("\uD83D\uDCAC Написать администратору") -> {
                userStateRepository.changeUserState("messageToAdmin", telegramObject);
                messageForAdmin();
            }
            default -> {
                String userStatus = userStateRepository.findByChatId(telegramObject.getId()).get().getUserState();

                switch (userStatus) {
                    case ("review") -> {
                        addReview();
                    }
                }
            }
        }
    }

    private void messageForAdmin() {
        String messageText = "Здесь Вы можете написать сообщение Управляющему! " +
                "Это может быть благодарность, отзыв, предложение, замечание, претензия и другое.";

        Optional<UserPhoneNumber> userPhoneNumber = userPhoneNumberRepository.findByChatId(telegramObject.getId());

        if (userPhoneNumber.isEmpty()) {
            userStateRepository.changeUserState("messageToAdminNONUMBER", telegramObject);

            messageText += "\n\nНо для начала пришлите пожалуйста нам свой номер телефона прожав кнопку ниже.";
        }

        userMessageSender.sendMessage(messageText);

    }

    private void addReview() {
        Optional<UserInDataBase> userInDataBase = userRepository.findById(telegramObject.getId());

        if (userInDataBase.isPresent()) {
            Review review = reviewService.createReview(userInDataBase.get(), telegramObject.getText());

            userStateRepository.changeUserState("main", telegramObject);
            userMessageSender.sendMessage("Спасибо за ваш отзыв");

            adminMessageSender.sendMessageToAllAdmin(telegramObject.getText(), telegramObject);

            log.debug("Review added: {}", review);
        } else {
            log.error("Failed to add review because user with id {} account was not found in the database", telegramObject.getId());
            userMessageSender.sendMessage("Произошла ошибка во время отправки отзыва. Пожалуйста, попробуйте позже.");
        }
    }
}
