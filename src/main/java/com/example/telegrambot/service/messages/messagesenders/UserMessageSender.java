package com.example.telegrambot.service.messages.messagesenders;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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

    public Message sendMessageWithChatId (Long chatId, String messageText) {
        getSendMessage().setChatId(chatId);

        var userState = userService.getUserState(chatId);
        setReplyKeyboardMarkupByUserStatus(userState);

        return super.sendMessage(messageText);
    }

    public Message sendMessageWithChatIdAndInlineKeyboard (Long chatId, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup){
        getSendMessage().setChatId(chatId);

        getSendMessage().setReplyMarkup(inlineKeyboardMarkup);

        return super.sendMessage(messageText);
    }

    /**
     * Sets the reply keyboard markup according to the user's status.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        UserStateEnum userState = userService.getUserState(telegramObject);

        setReplyKeyboardMarkupByUserStatus(userState);
    }

    private void setReplyKeyboardMarkupByUserStatus(UserStateEnum userState) {

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