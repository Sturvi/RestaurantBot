package com.example.telegrambot.service.messages.editmessagesender;

import com.example.telegrambot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@Scope("prototype")
public class EditMessageSender {
    private final TelegramBot telegramBot;
    private EditMessageText editMessageText;

    @Autowired
    public EditMessageSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.editMessageText = new EditMessageText();
    }

    public void editMessage(String text, Integer messageId, Long chatId) {
        prepareEditMessageText(text, messageId, chatId);

        executeEditMessage();
    }

    public void addInlineKeyboardAndEditMessage(String text, Integer messageId, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        prepareEditMessageText(text, messageId, chatId);

        editMessageText.setReplyMarkup(inlineKeyboardMarkup);

        executeEditMessage();
    }

    public void prepareEditMessageText(String text, Integer messageId, Long chatId) {
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(text);
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

    protected void newEditMessageText() {
        editMessageText = new EditMessageText();
    }

    protected EditMessageText getEditMessageText() {
        return editMessageText;
    }
}
