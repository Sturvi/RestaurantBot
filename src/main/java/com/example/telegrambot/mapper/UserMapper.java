package com.example.telegrambot.mapper;

import com.example.telegrambot.model.UserEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class UserMapper {

    public UserEntity mapToUserEntity(User user) {
        return UserEntity.builder()
                .chatId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .userStatus(true)
                .build();
    }
}