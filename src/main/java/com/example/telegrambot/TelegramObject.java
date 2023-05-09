package com.example.telegrambot;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramObject {

    private final BotApiObject botApiObject;
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



    public TelegramObject(BotApiObject botApiObject) {
        this.botApiObject = botApiObject;
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


    public String stringFrom() {
        User user = getFrom();

        var stringBuilder = new StringBuilder();

        stringBuilder.append(user.getFirstName()).append(" ");

        if (user.getLastName() != null){
            stringBuilder.append(user.getLastName()).append(", ");
        }

        stringBuilder.append("@").append(user.getUserName());

        return stringBuilder.toString();
    }

    public Boolean isMessage() {
        if (isMessage == null) initTelegramObject();
        return isMessage;
    }

    public Boolean isCallbackQuery() {
        if (isCallbackQuery == null) initTelegramObject();
        return isCallbackQuery;
    }

    public Long getId() {
        if (id == null) initTelegramObject();
        return id;
    }

    public Integer getMessageId() {
        if (messageId == null) initTelegramObject();
        return messageId;
    }

    public String getText() {
        if (text == null) initTelegramObject();
        return text;
    }

    public String getData() {
        if (data == null) initTelegramObject();
        return data;
    }

    public User getFrom() {
        if (from == null) initTelegramObject();
        return from;
    }

    public Boolean isContact() {
        if (isContact == null) initTelegramObject();
        return isContact;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) initTelegramObject();
        return phoneNumber;
    }

    public Contact getContact() {
        if (contact == null) initTelegramObject();
        return contact;
    }

    private void initTelegramObject() {
        isMessage = botApiObject instanceof Message;
        isCallbackQuery = botApiObject instanceof CallbackQuery;

        if (isMessage) {
            initMessageObject((Message) botApiObject);
        } else if (isCallbackQuery) {
            initCallbackQueryObject((CallbackQuery) botApiObject);
        }
    }

    private void initMessageObject(Message message) {
        id = message.getChatId();
        messageId = message.getMessageId();
        text = message.getText();
        data = null;
        from = message.getFrom();
        isContact = message.hasContact();
        contact = message.getContact();
        if (isContact) {
            phoneNumber = contact.getPhoneNumber();
        }
    }

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
}
