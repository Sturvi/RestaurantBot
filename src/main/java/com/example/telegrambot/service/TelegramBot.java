package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.handler.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents the Telegram bot that listens for updates and handles them accordingly.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot  {

    private final ExecutorService executorService;

    @Autowired
    private ApplicationContext applicationContext;

    public TelegramBot() {
        super("6272045013:AAGyZKGFDX_5E5jQALnj1FudvH2-yFxtQEs");
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * This method is called whenever an update is received by the bot.
     * It creates a new thread to handle the update and submits it to the executor service.
     *
     * @param update The update received by the bot.
     */
    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        Runnable newUserRequest = () -> {
            try {
                UpdateHandler updateHandler = applicationContext.getBean(UpdateHandler.class);
                updateHandler.handle(TelegramObject.getTelegramObject(update));
            } catch (Exception e) {
                e.printStackTrace(); // Вывод информации об исключении
            }
        };

        executorService.submit(newUserRequest);
    }

    /**
     * This method returns the username of the bot.
     *
     * @return The username of the bot.
     */
    @Override
    public String getBotUsername() {
        return "dafghrhsfjhfjytBOT";
    }
}
