package com.example.telegrambot;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramObject {

    private final BotApiObject botApiObject;

    public TelegramObject(BotApiObject botApiObject) {
        this.botApiObject = botApiObject;
    }

    public boolean isMessage() {
        return botApiObject instanceof Message;
    }

    public boolean isCallbackQuery() {
        return botApiObject instanceof CallbackQuery;
    }

    public Long getId() {
        if (botApiObject instanceof Message) return ((Message) botApiObject).getChatId();
        if (botApiObject instanceof CallbackQuery) return ((CallbackQuery) botApiObject).getFrom().getId();
        return null;
    }

    public Integer getMessageId() {
        if (botApiObject instanceof Message) return ((Message) botApiObject).getMessageId();
        if (botApiObject instanceof CallbackQuery) return ((CallbackQuery) botApiObject).getMessage().getMessageId();
        return null;
    }

    public String getText() {
        if (botApiObject instanceof Message) return ((Message) botApiObject).getText();
        if (botApiObject instanceof CallbackQuery) return ((CallbackQuery) botApiObject).getMessage().getText();
        return null;
    }

    public String getData() {
        if (botApiObject instanceof CallbackQuery) return ((CallbackQuery) botApiObject).getData();
        return null;
    }

    public User getFrom() {
        if (botApiObject instanceof CallbackQuery) return ((CallbackQuery) botApiObject).getFrom();
        if (botApiObject instanceof Message) return ((Message) botApiObject).getFrom();
        return null;
    }
}
