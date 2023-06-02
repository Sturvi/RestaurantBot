package com.example.telegrambot.service.keyboard;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Factory class for creating InlineKeyboardMarkup objects for admin messages.
 */
@Slf4j
public class AdminInlineKeyboardMarkupFactory extends InlineKeyboardMarkupFactory {

    private AdminInlineKeyboardMarkupFactory() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Creates an InlineKeyboardMarkup object for admin messages.
     *
     * @return InlineKeyboardMarkup object with buttons for viewing message history and replying to messages.
     */
    public static InlineKeyboardMarkup getInlineKeyboardForMessagesWithAdmin() {
        log.debug("Creating new InlineKeyboardMarkup for chat with admin messages");
        var inlineKeyboardMarkup = creatNewInlineKeyboard();

        addButtonToNewLine(inlineKeyboardMarkup, "📚💬 Предыдущие сообщения", "history");
        addButtonToNewLine(inlineKeyboardMarkup, "✉️ Ответить", "reply");

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getInlineKeyboardAfterPressHistoryButton() {
        log.debug("Creating new InlineKeyboardMarkup for chat with admin messages after history button");
        var inlineKeyboardMarkup = creatNewInlineKeyboard();

        addButtonToNewLine(inlineKeyboardMarkup, "✉️ Ответить", "reply");

        return inlineKeyboardMarkup;
    }
}
