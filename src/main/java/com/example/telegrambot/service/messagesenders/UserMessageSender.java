package com.example.telegrambot.service.messagesenders;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.UserStateService;
import com.example.telegrambot.service.keyboard.KeyboardMarkupFactory;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * This class is responsible for sending messages via the Telegram Bot API.
 */
@Slf4j
public class UserMessageSender extends MessageSender {
    private final TelegramObject telegramObject;
    private final UserStateService userStateService;

    public UserMessageSender(TelegramObject telegramObject, UserStateService userStateService) {
        this.telegramObject = telegramObject;
        this.userStateService = userStateService;
    }

    /**
     * Sends a message with the specified text to the user.
     *
     * @param text the text of the message
     * @return the message that was sent
     */
    public Message sendMessage(String text) {
        getSendMessage().setChatId(telegramObject.getId());
        setReplyKeyboardMarkupByUserStatus();
        return super.sendMessage(text);
    }

    /**
     * Sets the reply keyboard markup according to the user's status.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        String userStatus = userStateService.getUserStatus(telegramObject.getId());

        log.debug("Setting ReplyKeyboardMarkup for user status: {}", userStatus);
        getSendMessage().setReplyMarkup(KeyboardMarkupFactory.getReplyKeyboardMarkup(userStatus));
    }

/*    public void clean(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        newSendMessage();
    }

    public void setTelegramObject (TelegramObject telegramObject){
        clean(telegramObject);
    }*/
}