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
public class TelegramBot extends TelegramLongPollingBot {

    private final ExecutorService executorService;

    @Autowired
    // todo: Инжектить контекст в объект - плохая практика, говорящая о плохой архитектуре.
    private ApplicationContext applicationContext;


    public TelegramBot() {
        // todo: Все чувствительные данные нужно брать из свойств приложения.
        //       Создание бота прененести в конфигурацию, там же вытащить из свойств приложения
        //       все нужные данные с помощью аннотации @Value("${property.name}")
        //       https://www.baeldung.com/spring-value-annotation

        // (2) todo: Репозиторий доступен всем пользователям? Если да, то токен может взять кто-угодно и написать бота,
        //           ответственность за которого будет лежать на тебе, потому что токен принадлежит тебе
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
                // todo: плохая архитектура
                //       Прототипы нужно реализовывать по-другому
                //       https://www.google.com/search?q=spring+inject+prototype+bean+into+singleton&oq=spring+inject+prototype&aqs=chrome.1.69i57j0i512j0i20i263i512j0i22i30j69i60.7087j0j7&sourceid=chrome&ie=UTF-8
                //
                //        class Config {
                //
                //            @Bean
                //            public Supplier<SomeClass> getSomeClassSupplier() {
                //                return new SomeClass();
                //            }
                //
                //        }
                //
                //        class SomeService {
                //            @Autowired
                //            private final Supplier<SomeClass> someClassSupplier;
                //
                //            public void someMethod() {
                //                someClassSupplier.get().doSomething();
                //            }
                //        }

                UpdateHandler updateHandler = applicationContext.getBean(UpdateHandler.class);
                updateHandler.handle(TelegramObject.getTelegramObject(update));
            } catch (Exception e) {
                e.printStackTrace(); // Вывод информации об исключении
            }
        };

        // todo: Есть спринговый способ работы с асинхронными вызовами
        //       Нужно будет настроить executor в виде бина и навесить @Async на вызываемые методы.
        //       https://spring.io/guides/gs/async-method/
        executorService.submit(newUserRequest);
    }

    /**
     * This method returns the username of the bot.
     *
     * @return The username of the bot.
     */
    @Override
    public String getBotUsername() {
        // todo: Вынеси в конфигурацию.
        //       Пусть бот получает имя и токен из свойств приложения.
        return "dafghrhsfjhfjytBOT";
    }
}
