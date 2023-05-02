package com.example.telegrambot;

import com.example.telegrambot.model.UserInDataBase;
import com.example.telegrambot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

public class UpdateHandler {

    UserRepository userRepository;
    private final Update update;


    public UpdateHandler(Update update, UserRepository userRepository) {
        this.update = update;
        this.userRepository = userRepository;
    }

    public void handling(){

        if (isMessageWithText()) {
            var message = update.getMessage();
            updateUserInfo(message.getFrom());
        } else if (isCallbackWithData()) {
            var callbackQuery = update.getCallbackQuery();
            updateUserInfo(callbackQuery.getFrom());
        }
    }

    private boolean isMessageWithText() {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isCallbackWithData() {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty();
    }

    private void updateUserInfo (User user){
        UserInDataBase userInDataBase = UserInDataBase.builder()
                .chatId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .lastContact(LocalDateTime.now())
                .userStatus(true)
                .position("main")
                .build();

        userRepository.save(userInDataBase);
    }


}
