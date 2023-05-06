package com.example.telegrambot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;


public class KeyboardMarkupFactory {

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup(String keyboard) {
        switch (keyboard) {
            case ("main") -> {
                return getMainReplyKeyboardMarkup();
            }
            case ("review"), ("messageToAdmin") -> {
                return getCancelKeyboard();
            }
            case ("messageToAdminNONUMBER") -> {
                return getPhoneRequestKeyboard();
            }
            default -> {
                return null;
            }
        }
    }

    private static ReplyKeyboardMarkup getMainReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = creatKeyboard();

        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        // Добавление кнопок в первую строку
        keyboardRowList.add(new KeyboardRow());
        keyboardRowList.get(0).add(new KeyboardButton("\uD83D\uDCDD Оставить отзыв"));

        // Добавление кнопок во вторую строку
        keyboardRowList.add(new KeyboardRow());
        keyboardRowList.get(1).add(new KeyboardButton("\uD83D\uDCAC Написать администратору"));

        // Установка списка строк с кнопками для клавиатуры
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getCancelKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = creatKeyboard();

        addCancelButton(replyKeyboardMarkup);

        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getPhoneRequestKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = creatKeyboard();

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        // Создайте кнопку запроса номера телефона
        KeyboardButton requestPhoneNumber = new KeyboardButton("\uD83D\uDCF1 Поделиться номером телефона");
        requestPhoneNumber.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(requestPhoneNumber);
        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        addCancelButton(replyKeyboardMarkup);

        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup creatKeyboard () {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(new ArrayList<KeyboardRow>());

        return replyKeyboardMarkup;
    }

    private static void addCancelButton (ReplyKeyboardMarkup replyKeyboardMarkup){
        List<KeyboardRow> keyboardRowList = replyKeyboardMarkup.getKeyboard();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("⛔ Отмена"));
        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }
}
