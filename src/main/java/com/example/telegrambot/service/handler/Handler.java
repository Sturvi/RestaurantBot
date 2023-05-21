package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;

interface Handler {

    void handle(TelegramObject telegramObject);
}
