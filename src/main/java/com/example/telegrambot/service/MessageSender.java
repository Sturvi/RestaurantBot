package com.example.telegrambot.service;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class MessageSender {
    private static final Logger LOGGER = Logger.getLogger(MessageSender.class);

    private SendMessage sendMessage;

    /**
     * Конструктор MessageSender инициализирует объект SendMessage.
     */
    protected MessageSender() {
        this.sendMessage = new SendMessage();
    }

    /**
     * Отправляет сообщение с указанным текстом и ID сообщения для ответа.
     *
     * @param text      текст сообщения
     * @param messageId ID сообщения для ответа
     * @return объект Message с результатами отправки сообщения
     */
    protected Message sendMessage(String text, Integer messageId) {
        sendMessage.setReplyToMessageId(messageId);
        return sendMessage(text);
    }

    /**
     * Отправляет сообщение с указанным текстом.
     *
     * @param text текст сообщения
     * @return объект Message с результатами отправки сообщения
     */
    protected Message sendMessage(String text) {
        sendMessage.setText(text);

        LOGGER.info(String.format("Preparing to send message with chat ID: %s, text: %s", sendMessage.getChatId(), sendMessage.getText()));
        return executeMessage();
    }

    /**
     * Выполняет отправку сообщения с помощью Telegram Bot API.
     *
     * @return объект Message с результатами отправки сообщения
     */
    protected Message executeMessage() {
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
     * Создает новый объект SendMessage.
     */
    protected void newSendMessage() {
        sendMessage = new SendMessage();
    }

    /**
     * Возвращает текущий объект SendMessage.
     *
     * @return текущий объект SendMessage
     */
    protected SendMessage getSendMessage() {
        return sendMessage;
    }
}