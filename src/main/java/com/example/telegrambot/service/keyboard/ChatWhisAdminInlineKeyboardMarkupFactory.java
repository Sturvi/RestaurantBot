package com.example.telegrambot.service.keyboard;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Factory class for creating InlineKeyboardMarkup objects for admin messages.
 */
@Slf4j
public class ChatWhisAdminInlineKeyboardMarkupFactory extends InlineKeyboardMarkupFactory {

    private ChatWhisAdminInlineKeyboardMarkupFactory() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Creates an InlineKeyboardMarkup object for admin messages.
     *
     * @return InlineKeyboardMarkup object with buttons for viewing message history and replying to messages.
     */
    public static InlineKeyboardMarkup getInlineKeyboardForMessagesWithAdmin(Long chatId) {
        log.debug("Creating new InlineKeyboardMarkup for chat with admin messages");
        var inlineKeyboardMarkup = creatNewInlineKeyboard();

        addButtonToNewLine(inlineKeyboardMarkup, "📚💬 Предыдущие сообщения", "history " + chatId);
        addButtonToNewLine(inlineKeyboardMarkup, "✉️ Ответить", "reply " + chatId);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getInlineKeyboardAfterPressHistoryButton(Long chatId) {
        log.debug("Creating new InlineKeyboardMarkup for chat with admin messages after history button");
        var inlineKeyboardMarkup = creatNewInlineKeyboard();

        addButtonToNewLine(inlineKeyboardMarkup, "✉️ Ответить", "reply " + chatId);

        return inlineKeyboardMarkup;
    }
}
