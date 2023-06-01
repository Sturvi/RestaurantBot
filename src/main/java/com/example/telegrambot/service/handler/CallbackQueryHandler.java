package com.example.telegrambot.service.handler;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.service.handler.eventhandlers.ChatEventCallbackQueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class CallbackQueryHandler implements Handler {

    private final ChatEventCallbackQueryHandler callbackQueryHandler;

    private TelegramObject telegramObject;
    private final Map<String, Handler> callbackHandlers = new HashMap<>();

    private void init (TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        callbackHandlers.put("reply", callbackQueryHandler);
        callbackHandlers.put("history", callbackQueryHandler);
    }

    @Override
    public void handle(TelegramObject telegramObject) {
        init(telegramObject);

        var handler = callbackHandlers.get(telegramObject.getData());

        if (handler != null) {
            handler.handle(telegramObject);
        } else {
            log.error("No handler found for key (CallbackData): " + telegramObject.getData());
        }
    }


}

