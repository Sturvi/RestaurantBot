package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * The UpdateHandler class is responsible for handling incoming updates from the Telegram API.
 * It updates user information in the UserRepository and delegates the handling of the update to
 * the appropriate handler based on the type of update.
 */
@Component
@Scope("prototype")
@Slf4j
public class UpdateHandler implements Handler{

    private final ApplicationContext applicationContext;
    private final UserRepository userRepository;

    /**
     * Constructs a new UpdateHandler with the given ApplicationContext and UserRepository.
     *
     * @param applicationContext the ApplicationContext to use for retrieving handler beans
     * @param userRepository the UserRepository to use for updating user information
     */
    @Autowired
    public UpdateHandler(ApplicationContext applicationContext, UserRepository userRepository) {
        this.applicationContext = applicationContext;
        this.userRepository = userRepository;
    }

    /**
     * Handles the given TelegramObject by updating user information in the UserRepository and
     * delegating the handling of the update to the appropriate handler based on the type of update.
     *
     * @param telegramObject the TelegramObject to handle
     */
    @Override
    public void handle(TelegramObject telegramObject) {
        userRepository.updateUserInfo(telegramObject.getFrom());

        try {
            if (telegramObject.isMessage()) {
                applicationContext.getBean(MessageUpdateHandler.class).handle(telegramObject);
            } else if (telegramObject.isCallbackQuery()) {
                log.debug("Handling update for callback with data: {}, chat ID: {}", telegramObject.getData(), telegramObject.getId());
            }
        } catch (Exception e) {
            log.error("An error occurred while handling the Telegram object", e);
        }
    }
}
