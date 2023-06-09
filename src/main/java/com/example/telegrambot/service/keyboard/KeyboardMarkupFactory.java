package com.example.telegrambot.service.keyboard;

import com.example.telegrambot.model.UserStateEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardMarkupFactory {

    private KeyboardMarkupFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a ReplyKeyboardMarkup object based on the specified user state.
     *
     * @param userStateEnum the user state to create the keyboard for
     * @return a ReplyKeyboardMarkup object based on the specified user state
     */
    public static ReplyKeyboardMarkup getReplyKeyboardMarkup(UserStateEnum userStateEnum) {
        switch (userStateEnum) {
            case MAIN -> {
                return getMainReplyKeyboardMarkup();
            }
            case REVIEW, MESSAGE_TO_ADMIN -> {
                return getCancelKeyboard();
            }
            case REQUEST_PHONE_NUMBER -> {
                return getPhoneRequestKeyboard();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Returns a ReplyKeyboardMarkup object with the main keyboard layout.
     *
     * @return a ReplyKeyboardMarkup object with the main keyboard layout
     */
    private static ReplyKeyboardMarkup getMainReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard();

        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        // Add buttons to the first row
        keyboardRowList.add(new KeyboardRow());
        keyboardRowList.get(0).add(new KeyboardButton("\uD83D\uDCDD Оставить отзыв"));

        // Add buttons to the second row
        keyboardRowList.add(new KeyboardRow());
        keyboardRowList.get(1).add(new KeyboardButton("\uD83D\uDCAC Написать администратору"));

        // Set the list of rows with buttons for the keyboard
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }

    /**
     * Returns a ReplyKeyboardMarkup object with a cancel button.
     *
     * @return a ReplyKeyboardMarkup object with a cancel button
     */
    private static ReplyKeyboardMarkup getCancelKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard();

        addCancelButton(replyKeyboardMarkup);

        return replyKeyboardMarkup;
    }

    /**
     * Returns a ReplyKeyboardMarkup object with a button to request the user's phone number.
     *
     * @return a ReplyKeyboardMarkup object with a button to request the user's phone number
     */
    private static ReplyKeyboardMarkup getPhoneRequestKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = createKeyboard();

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        // Create a button to request the phone number
        KeyboardButton requestPhoneNumber = new KeyboardButton("\uD83D\uDCF1 Поделиться номером телефона");
        requestPhoneNumber.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(requestPhoneNumber);
        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        addCancelButton(replyKeyboardMarkup);

        return replyKeyboardMarkup;
    }

    /**
     * Creates a new ReplyKeyboardMarkup object.
     *
     * @return a new ReplyKeyboardMarkup object
     */
    private static ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(new ArrayList<>());

        return replyKeyboardMarkup;
    }

    /**
     * Adds a cancel button to the specified ReplyKeyboardMarkup object.
     *
     * @param replyKeyboardMarkup the ReplyKeyboardMarkup object to add the cancel button to
     */
    private static void addCancelButton(ReplyKeyboardMarkup replyKeyboardMarkup) {
        List<KeyboardRow> keyboardRowList = replyKeyboardMarkup.getKeyboard();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("⛔ Отмена"));
        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}