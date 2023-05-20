package com.example.telegrambot;

import com.example.telegrambot.service.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Главный класс приложения, который запускает Telegram-бота.
 */
@SpringBootApplication
@Slf4j
public class TelegramBotApplication {

    private final TelegramBot telegramBot;

    /**
     * Конструктор класса TelegramBotApplication.
     *
     * @param telegramBot объект TelegramBot, который будет зарегистрирован в Telegram.
     */
    public TelegramBotApplication(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Главный метод, который запускает приложение.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    /**
     * Метод, который регистрирует Telegram-бота в Telegram.
     */
    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
            log.debug("Telegram bot registered successfully.");
        } catch (TelegramApiException e) {
            log.error("Error occurred while registering Telegram bot: {}", e.getMessage());
        }
    }
}