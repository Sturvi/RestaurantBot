package com.example.telegrambot.service.messages.messagesenders;

import com.example.telegrambot.service.AdministratorList;
import com.example.telegrambot.service.TelegramBot;
import com.example.telegrambot.service.keyboard.ChatWhisAdminInlineKeyboardMarkupFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class AdminMessageSender extends MessageSender {

    private final AdministratorList administratorList;


    @Autowired
    public AdminMessageSender(TelegramBot telegramBot, AdministratorList administratorList) {
        super(telegramBot);
        log.debug("AdminMessageSender initialized with TelegramBot and AdministratorList");
        this.administratorList = administratorList;
    }

    public void prepareAndSendAdminMessage(String messageText) {
        log.debug("Preparing and sending admin message with text: {}", messageText);
        setText(messageText);

        deliverMessageToAdmins();
    }

    public void prepareAndSendChatMessageToAllAdmins(String messageText){
        log.debug("Preparing and sending chat message to all admins with text: {}", messageText);
        var keyboard = ChatWhisAdminInlineKeyboardMarkupFactory.getInlineKeyboardForMessagesWithAdmin();
        log.debug("Inline keyboard markup for admins created");

        getSendMessage().setReplyMarkup(keyboard);
        log.debug("Reply markup set for SendMessage");

        setText(messageText);
        log.debug("Message text set");

        deliverMessageToAdmins();
    }

    private void deliverMessageToAdmins() {
        for (Long adminId : administratorList.getAdministratorsIdList()) {
            log.debug("Processing admin with chat ID: {}", adminId);
            getSendMessage().setChatId(adminId);
            log.debug("Chat ID set for SendMessage");

            executeMessage();
            log.debug("Sent message to admin with chat ID: {}", adminId);
        }
    }

    public void sendMessageToAdmin(Long adminChatId, String messageText) {
        if (administratorList.hasAdmin(adminChatId)) {
            getSendMessage().setChatId(adminChatId);

            setText(messageText);

            executeMessage();
        } else {
            log.error("Message to user {} and text \"{}\" was not delivered because this user is not an administrator.", adminChatId, messageText);
        }
    }
}
