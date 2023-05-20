package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;

interface Handler {

    public void handle(TelegramObject telegramObject);
}
