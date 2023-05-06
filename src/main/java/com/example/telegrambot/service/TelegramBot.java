package com.example.telegrambot.service;

import com.example.telegrambot.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TelegramBot extends TelegramLongPollingBot  {

    private final ExecutorService executorService;

    @Autowired
    private ApplicationContext applicationContext;

    public TelegramBot() {
        super("6272045013:AAGyZKGFDX_5E5jQALnj1FudvH2-yFxtQEs");
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        Runnable newUserRequest = () -> {
            try {
                UpdateHandler updateHandler = applicationContext.getBean(UpdateHandler.class);
                updateHandler.setTelegramObject(update);
                updateHandler.handling();
            } catch (Exception e) {
                e.printStackTrace(); // Вывод информации об исключении
            }
        };


        executorService.submit(newUserRequest);
    }

    @Override
    public String getBotUsername() {
        return "dafghrhsfjhfjytBOT";
    }
}
