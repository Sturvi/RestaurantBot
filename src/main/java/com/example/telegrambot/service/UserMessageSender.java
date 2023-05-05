package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.keyboard.KeyboardMarkupFactory;
import com.example.telegrambot.repository.AllRepository;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


/**
 * Класс для отправки сообщений через Telegram Bot API.
 */
public class UserMessageSender extends MessageSender {
    private static final Logger LOGGER = Logger.getLogger(UserMessageSender.class);
    private final TelegramObject telegramObject;
    private final AllRepository allRepository;


    /**
     * Конструктор класса MessageSender для создания экземпляра с указанными параметрами.
     *
     * @param telegramObject объект {@link TelegramObject}, содержащий информацию о пользователе.
     * @param allRepository  объект {@link AllRepository} для доступа к репозиторию пользователей.
     */
    public UserMessageSender(TelegramObject telegramObject, AllRepository allRepository) {
        super();
        this.telegramObject = telegramObject;
        this.allRepository = allRepository;
    }

    public Message sendMessage(String text) {
        getSendMessage().setChatId(telegramObject.getId());
        setReplyKeyboardMarkupByUserStatus();
        return super.sendMessage(text);
    }

    /**
     * Устанавливает клавиатуру согласно статусу пользователя.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        String userStatus = allRepository.getUserStateRepository().findById(telegramObject.getId()).get().getUserState();

        LOGGER.info(String.format("Setting ReplyKeyboardMarkup for user status: %s", userStatus));
        getSendMessage().setReplyMarkup(KeyboardMarkupFactory.getReplyKeyboardMarkup(userStatus));
    }
}
