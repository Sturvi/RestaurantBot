package com.example.telegrambot.service.keyboard;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class InlineKeyboardMarkupFactory {

    private InlineKeyboardMarkupFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static InlineKeyboardMarkup getAdminInlineKeyboardForMessages() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(new ArrayList<>());
        keyboard.add(new ArrayList<>());

        InlineKeyboardButton messagesHistory = new InlineKeyboardButton("\uD83D\uDCDA\uD83D\uDCAC Предыдущие сообщения");
        messagesHistory.setCallbackData("history");
        keyboard.get(0).add(messagesHistory);

        InlineKeyboardButton reply = new InlineKeyboardButton("✉️ Ответить");
        reply.setCallbackData("reply");
        keyboard.get(1).add(reply);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        log.debug("Created admin inline keyboard for messages");

        return inlineKeyboardMarkup;
    }
}
