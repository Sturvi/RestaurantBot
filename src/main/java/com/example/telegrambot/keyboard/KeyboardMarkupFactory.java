package com.example.telegrambot.keyboard;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardMarkupFactory {

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup(String userStatus) {
        switch (userStatus) {
            case ("main") -> {
                return getMainReplyKeyboardMarkup();
            }
            case ("review") -> {
                return getReviewReplyKeyboardMarkup();
            }
            default -> {
                return null;
            }
        }
    }
    private static ReplyKeyboardMarkup getMainReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        // Добавление кнопок в первую строку
        keyboardFirstRow.add(new KeyboardButton("\uD83D\uDCDD Оставить отзыв"));

        // Добавление строк с кнопками в список
        keyboardRowList.add(keyboardFirstRow);

        // Установка списка строк с кнопками для клавиатуры
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getReviewReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        // Добавление кнопок в первую строку
        keyboardFirstRow.add(new KeyboardButton("\uD83D\uDD19 Назад"));

        // Добавление строк с кнопками в список
        keyboardRowList.add(keyboardFirstRow);

        // Установка списка строк с кнопками для клавиатуры
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }
}
