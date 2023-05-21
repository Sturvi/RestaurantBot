package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.handler.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.context.ApplicationContext;


import java.util.function.Supplier;

/**
 * This class represents the Telegram bot that listens for updates and handles them accordingly.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final Supplier<UpdateHandler> updateHandlerSupplier;

    @Autowired
    public TelegramBot(@Value("${bot.token}") String botToken,
                       @Value("${bot.username}") String botUsername,
                       Supplier<UpdateHandler> updateHandlerSupplier) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandlerSupplier = updateHandlerSupplier;
    }

    /**
     * This method is called whenever an update is received by the bot.
     * It creates a new thread to handle the update.
     *
     * @param update The update received by the bot.
     */
    @Override
    @Transactional
    @Async("threadPoolTaskExecutor")
    public void onUpdateReceived(Update update) {
        try {
            UpdateHandler updateHandler = updateHandlerSupplier.get();
            updateHandler.handle(TelegramObject.getTelegramObject(update));
        } catch (Exception e) {
            e.printStackTrace(); // Вывод информации об исключении
        }
    }

    /**
     * This method returns the username of the bot.
     *
     * @return The username of the bot.
     */
    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Configuration
    public static class TelegramBotConfig {

        private final ApplicationContext applicationContext;

        public TelegramBotConfig(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        public Supplier<UpdateHandler> updateHandlerSupplier() {
            return () -> applicationContext.getBean(UpdateHandler.class);
        }

        @Bean(name = "threadPoolTaskExecutor")
        public ThreadPoolTaskExecutor getExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(10);
            executor.setWaitForTasksToCompleteOnShutdown(true);
            return executor;
        }
    }
}
