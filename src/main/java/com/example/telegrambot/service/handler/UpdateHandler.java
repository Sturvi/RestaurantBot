package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class UpdateHandler implements Handler{

    private final UserService userService;
    private final MessageUpdateHandler messageUpdateHandler;
    private final CallbackQueryHandler callbackQueryHandler;


    /**
     * Handles the given TelegramObject by updating user information in the UserRepository and
     * delegating the handling of the update to the appropriate handler based on the type of update.
     *
     * @param telegramObject the TelegramObject to handle
     */
    @Override
    public void handle(TelegramObject telegramObject) {
        userService.saveOrUpdateUser(telegramObject.getFrom());

        try {
            if (telegramObject.isMessage()) {
                messageUpdateHandler.handle(telegramObject);
            } else if (telegramObject.isCallbackQuery()) {
                log.debug("Handling update for callback with data: {}, chat ID: {}", telegramObject.getData(), telegramObject.getId());
                callbackQueryHandler.handle(telegramObject);
            }
        } catch (Exception e) {
            log.error("An error occurred while handling the Telegram object", e);
        }
    }
}
