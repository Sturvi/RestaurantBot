package com.example.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.*;

/**
 * This class represents a Telegram object that can be either a message or a callback query.
 * It provides methods to initialize the object and get its properties.
 */
@Slf4j
public class TelegramObject {

    private BotApiObject botApiObject;
    private Boolean isMessage;
    private Boolean isCallbackQuery;
    private Boolean isContact;
    private Long id;
    private Integer messageId;
    private String text;
    private String data;
    private User from;
    private Contact contact;
    private String phoneNumber;

    private TelegramObject() {
        this.botApiObject = null;
        this.isMessage = null;
        this.isCallbackQuery = null;
        this.isContact = null;
        this.id = null;
        this.messageId = null;
        this.text = null;
        this.data = null;
        this.from = null;
        this.contact = null;
        this.phoneNumber = null;
    }

    /**
     * Initializes the Telegram object based on the given update.
     *
     * @param update the update to initialize the object from
     * @return the initialized Telegram object
     */
    public static TelegramObject getTelegramObject(Update update) {
        TelegramObject telegramObject = new TelegramObject();

        if (isMessageWithText(update)) {
            telegramObject.botApiObject = update.getMessage();
        } else if (isCallbackWithData(update)) {
            telegramObject.botApiObject = update.getCallbackQuery();
        }
        telegramObject.initTelegramObject();

        return telegramObject;
    }

    /**
     * Returns a string representation of the user who sent the message or callback query.
     *
     * @return the string representation of the user
     */
    public String stringFrom() {
        User user = getFrom();

        var stringBuilder = new StringBuilder();

        stringBuilder.append(user.getFirstName()).append(" ");

        if (user.getLastName() != null) {
            stringBuilder.append(user.getLastName()).append(", ");
        }

        stringBuilder.append("@").append(user.getUserName());

        return stringBuilder.toString();
    }

    /**
     * Returns true if the Telegram object is a message, false otherwise.
     *
     * @return true if the Telegram object is a message, false otherwise
     */
    public Boolean isMessage() {
        return isMessage;
    }

    /**
     * Returns true if the Telegram object is a callback query, false otherwise.
     *
     * @return true if the Telegram object is a callback query, false otherwise
     */
    public Boolean isCallbackQuery() {
        return isCallbackQuery;
    }

    /**
     * Returns the chat ID of the message or callback query.
     *
     * @return the chat ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the message ID of the message or callback query.
     *
     * @return the message ID
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * Returns the text of the message or callback query.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the data of the callback query.
     *
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * Returns the user who sent the message or callback query.
     *
     * @return the user
     */
    public User getFrom() {
        return from;
    }

    /**
     * Returns true if the message contains a contact, false otherwise.
     *
     * @return true if the message contains a contact, false otherwise
     */
    public Boolean isContact() {
        return isContact;
    }

    /**
     * Returns the phone number of the contact in the message.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the contact in the message.
     *
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Initializes the Telegram object based on the type of the bot API object.
     */
    private void initTelegramObject() {
        isMessage = botApiObject instanceof Message;
        isCallbackQuery = botApiObject instanceof CallbackQuery;

        if (Boolean.TRUE.equals(isMessage)) {
            initMessageObject((Message) botApiObject);
        } else if (Boolean.TRUE.equals(isCallbackQuery)) {
            initCallbackQueryObject((CallbackQuery) botApiObject);
        }
    }

    /**
     * Initializes the message object based on the given message.
     *
     * @param message the message to initialize the object from
     */
    private void initMessageObject(Message message) {
        id = message.getChatId();
        messageId = message.getMessageId();
        text = message.getText();
        data = null;
        from = message.getFrom();
        isContact = message.hasContact();
        contact = message.getContact();
        if (Boolean.TRUE.equals(isContact)) {
            phoneNumber = contact.getPhoneNumber();
        }
    }

    /**
     * Initializes the callback query object based on the given callback query.
     *
     * @param callbackQuery the callback query to initialize the object from
     */
    private void initCallbackQueryObject(CallbackQuery callbackQuery) {
        id = callbackQuery.getFrom().getId();
        messageId = callbackQuery.getMessage().getMessageId();
        text = callbackQuery.getMessage().getText();
        data = callbackQuery.getData();
        from = callbackQuery.getFrom();
        isContact = false;
        contact = null;
        phoneNumber = null;
    }

    private static boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage();
    }

     private static boolean isCallbackWithData(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty();
    }
}