package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.keyboard.KeyboardMarkupFactory;
import com.example.telegrambot.repository.AllRepository;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для отправки сообщений через Telegram Bot API.
 */
public class MessageSender {
    private static final Logger LOGGER = Logger.getLogger(MessageSender.class);
    private final TelegramObject telegramObject;
    private final AllRepository allRepository;

    private SendMessage sendMessage;

    /**
     * Конструктор класса MessageSender для создания экземпляра с указанными параметрами.
     *
     * @param telegramObject объект {@link TelegramObject}, содержащий информацию о пользователе.
     * @param allRepository  объект {@link AllRepository} для доступа к репозиторию пользователей.
     */
    public MessageSender(TelegramObject telegramObject, AllRepository allRepository) {
        this.telegramObject = telegramObject;
        this.allRepository = allRepository;
        this.sendMessage = new SendMessage();
        this.sendMessage.setChatId(telegramObject.getId());
    }


    /**
     * Устанавливает текст отправляемого сообщения.
     *
     * @param text текст сообщения.
     */
    public void setText(String text) {
        sendMessage.setText(text);
    }

    /**
     * Отправляет сообщение с заданным текстом.
     *
     * @param text текст сообщения.
     * @return объект {@link Message} с информацией об отправленном сообщении.
     */
    public Message sendMessage(String text) {
        return sendMessage(text, false);
    }

    /**
     * Отправляет сообщение с заданным текстом и возможностью установки идентификатора ответа.
     *
     * @param text                текст сообщения.
     * @param setReplyToMessageId флаг, указывающий на необходимость установки идентификатора ответа.
     * @return объект {@link Message} с информацией об отправленном сообщении.
     */
    public Message sendMessage(String text, boolean setReplyToMessageId) {

        sendMessage.setText(text);

        if (setReplyToMessageId) {
            sendMessage.setReplyToMessageId(telegramObject.getMessageId());
        }

        setReplyKeyboardMarkupByUserStatus();

        LOGGER.info(String.format("Preparing to send message with chat ID: %s, text: %s", sendMessage.getChatId(), sendMessage.getText()));
        return executeMessage();
    }

    /**
     * Отправляет сообщение с установленными параметрами через Telegram Bot API.
     *
     * @return объект {@link Message} с информацией об отправленном сообщении.
     */
    public Message executeMessage() {
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);

        LOGGER.info(String.format("Sending message with chat ID: %s, text: %s", sendMessage.getChatId(), sendMessage.getText()));

        Message message = null;
        try {
            message = new TelegramBot().execute(sendMessage);
            LOGGER.info(String.format("Successfully sent message to chat ID: %s, with message ID: %d", message.getChatId(), message.getMessageId()));
        } catch (TelegramApiException e) {
            LOGGER.error(String.format("Error sending message to user: %s", e.getMessage()));
            e.printStackTrace();
        }

        return message;
    }

    /**
     * Устанавливает клавиатуру согласно статусу пользователя.
     */
    private void setReplyKeyboardMarkupByUserStatus() {
        String userStatus = allRepository.getUserStateRepository().findById(telegramObject.getId()).get().getUserState();

        LOGGER.info(String.format("Setting ReplyKeyboardMarkup for user status: %s", userStatus));
        sendMessage.setReplyMarkup(KeyboardMarkupFactory.getReplyKeyboardMarkup(userStatus));
    }
}
