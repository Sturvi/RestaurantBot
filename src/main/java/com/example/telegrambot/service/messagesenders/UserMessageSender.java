package com.example.telegrambot.service.messagesenders;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.service.TelegramBot;
import com.example.telegrambot.service.UserService;
import com.example.telegrambot.service.keyboard.KeyboardMarkupFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * This class is responsible for sending messages via the Telegram Bot API.
 */
@Slf4j
@Component
@Scope("prototype")
public class UserMessageSender extends MessageSender {
    private TelegramObject telegramObject;
    private final UserService userService;

    @Autowired
    public UserMessageSender(TelegramBot telegramBot, UserService userService) {
        super(telegramBot);
        this.userService = userService;
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
        UserStateEnum userState = userService.getUserStatus(telegramObject);

        log.debug("Setting ReplyKeyboardMarkup for user status: {}", userState);
        getSendMessage().setReplyMarkup(KeyboardMarkupFactory.getReplyKeyboardMarkup(userState));
    }

    public void clean(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        newSendMessage();
    }

    public void setTelegramObject (TelegramObject telegramObject){
        clean(telegramObject);
    }
}