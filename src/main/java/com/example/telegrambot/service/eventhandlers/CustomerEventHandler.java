package com.example.telegrambot.service.eventhandlers;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.ReviewEntity;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.service.ReviewService;
import com.example.telegrambot.service.UserStateService;
import com.example.telegrambot.service.messagesenders.AdminMessageSender;
import com.example.telegrambot.service.messagesenders.UserMessageSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class CustomerEventHandler {

    private final AdminMessageSender adminMessageSender;
    private final UserRepository userRepository;
    private final ReviewService reviewService;
    private final UserStateService userStateService;
    private final UserMessageSender userMessageSender;

    /**
     * Handles a new customer review.
     *
     * @param telegramObjectWishNewReview the TelegramObject containing information about the new review
     */
    public void handleNewCustomerReview(TelegramObject telegramObjectWishNewReview) {
        Optional<UserEntity> userInDataBase = userRepository.findByChatId(telegramObjectWishNewReview.getId());

        if (userInDataBase.isPresent()) {
            ReviewEntity reviewEntity = reviewService.createReview(userInDataBase.get(), telegramObjectWishNewReview.getText());

            userStateService.changeUserState(UserStateEnum.MAIN, telegramObjectWishNewReview.getId());
            userMessageSender.sendMessage("Спасибо за ваш отзыв");

            sendNewReviewNotificationToAdmins(telegramObjectWishNewReview);

            log.debug("Review added: {}", reviewEntity);
        } else {
            handleUserNotFoundInDatabase(telegramObjectWishNewReview);
        }
    }

    /**
     * Sends a notification to all admins about a new customer review.
     *
     * @param telegramObjectWishNewReview the TelegramObject containing information about the new review
     */
    private void sendNewReviewNotificationToAdmins(TelegramObject telegramObjectWishNewReview) {
        String messageText = "ОСТАВЛЕН НОВЫЙ ОТЗЫВ!\n\n" + telegramObjectWishNewReview.stringFrom() + ": \n\n" + telegramObjectWishNewReview.getText();
        adminMessageSender.sendMessageToAllAdmin(messageText);
        log.debug("Sent new customer review notification to all admins");
    }

    /**
     * Handles the case where a user is not found in the database.
     *
     * @param telegramObjectWishNewReview the TelegramObject containing information about the new review
     */
    private void handleUserNotFoundInDatabase(TelegramObject telegramObjectWishNewReview) {
        log.error("Failed to add review because user with id {} account was not found in the database", telegramObjectWishNewReview.getId());
        userMessageSender.sendMessage("Произошла ошибка во время отправки отзыва. Пожалуйста, попробуйте позже.");
    }
}