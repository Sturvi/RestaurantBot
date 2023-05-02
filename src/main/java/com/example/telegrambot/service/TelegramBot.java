package com.example.telegrambot.service;

import com.example.telegrambot.UpdateHandler;
import com.example.telegrambot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TelegramBot extends TelegramLongPollingBot  {

    @Autowired
    UserRepository userRepository;
    private final ExecutorService executorService;

    public TelegramBot() {
        super("6272045013:AAGyZKGFDX_5E5jQALnj1FudvH2-yFxtQEs");
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        Runnable newUserRequest = () -> {
            new UpdateHandler(update, userRepository).handling();
        };

        executorService.submit(newUserRequest);
    }

    @Override
    public String getBotUsername() {
        return "dafghrhsfjhfjytBOT";
    }
}
