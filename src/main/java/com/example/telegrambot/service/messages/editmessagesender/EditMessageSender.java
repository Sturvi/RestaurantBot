package com.example.telegrambot.service.messages.editmessagesender;

import com.example.telegrambot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@Scope("prototype")
public abstract class EditMessageSender {
    private final TelegramBot telegramBot;
    private EditMessageText editMessageText;

    @Autowired
    protected EditMessageSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.editMessageText = new EditMessageText();
    }

    protected void editMessage(String text, Integer messageId, Long chatId) {
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessage(text);
    }

    protected void editMessage(String text) {
        editMessageText.setText(text);

        log.debug("Preparing to edit message with chat ID: {}, text: {}", editMessageText.getChatId(), editMessageText.getText());
        executeEditMessage();
    }

    protected void setText(String text) {
        editMessageText.setText(text);
    }

    protected void setChatId(Long chatId) {
        editMessageText.setChatId(String.valueOf(chatId));
    }

    protected void setMessageId(Integer messageId) {
        editMessageText.setMessageId(messageId);
    }

    protected void executeEditMessage() {
        editMessageText.enableMarkdown(true);
        editMessageText.enableHtml(true);

        log.debug("Editing message with chat ID: {}, text: {}", editMessageText.getChatId(), editMessageText.getText());

        try {
            telegramBot.execute(editMessageText);
            log.debug("Successfully edited message in chat ID: {}, with message ID: {}", editMessageText.getChatId(), editMessageText.getMessageId());
        } catch (TelegramApiException e) {
            log.debug("Error editing message: {}", e.getMessage());
            e.printStackTrace();
        }

    }

    protected synchronized void newEditMessageText() {
        editMessageText = new EditMessageText();
    }

    protected synchronized EditMessageText getEditMessageText() {
        return editMessageText;
    }
}
