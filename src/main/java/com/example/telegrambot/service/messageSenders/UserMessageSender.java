package com.example.telegrambot.service.messageSenders;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.UserStateService;
import com.example.telegrambot.service.keyboard.KeyboardMarkupFactory;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;


/**
 * Класс для отправки сообщений через Telegram Bot API.
 */
public class UserMessageSender extends MessageSender {
    private static final Logger LOGGER = Logger.getLogger(UserMessageSender.class);
    private final TelegramObject telegramObject;
    private final UserStateService userStateService;

    public UserMessageSender(TelegramObject telegramObject, UserStateService userStateService) {
        this.telegramObject = telegramObject;
        this.userStateService = userStateService;
    }


    public Message sendMessage(String text) {
        getSendMessage().setChatId(telegramObject.getId());
        setReplyKeyboardMarkupByUserStatus();
        return super.sendMessage(text);
    }

    /**
     * Устанавливает клавиатуру согласно статусу пользователя.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        String userStatus = userStateService.getUserStatus(telegramObject.getId());

        LOGGER.info(String.format("Setting ReplyKeyboardMarkup for user status: %s", userStatus));
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
