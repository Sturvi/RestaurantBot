package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.keyboard.KeyboardMarkupFactory;
import com.example.telegrambot.repository.UserStateRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;


/**
 * Класс для отправки сообщений через Telegram Bot API.
 */
@Component
@Scope("prototype")
public class UserMessageSender extends MessageSender {
    private static final Logger LOGGER = Logger.getLogger(UserMessageSender.class);
    private TelegramObject telegramObject;

    @Autowired
    private UserStateRepository userStateRepository;


    public Message sendMessage(String text) {
        getSendMessage().setChatId(telegramObject.getId());
        setReplyKeyboardMarkupByUserStatus();
        return super.sendMessage(text);
    }

    /**
     * Устанавливает клавиатуру согласно статусу пользователя.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        String userStatus = userStateRepository.findById(telegramObject.getId()).get().getUserState();

        LOGGER.info(String.format("Setting ReplyKeyboardMarkup for user status: %s", userStatus));
        getSendMessage().setReplyMarkup(KeyboardMarkupFactory.getReplyKeyboardMarkup(userStatus));
    }

    public void clean(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        newSendMessage();
    }

    public void setTelegramObject (TelegramObject telegramObject){
        clean(telegramObject);
    }
}
