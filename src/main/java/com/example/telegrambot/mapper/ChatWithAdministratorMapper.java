package com.example.telegrambot.mapper;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.ChatWithAdministratorEntity;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserRoleEnum;
import com.example.telegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatWithAdministratorMapper {

    private final UserService userService;

    public ChatWithAdministratorEntity newMessageFromUserToAdmin(TelegramObject telegramObject) {
        return ChatWithAdministratorEntity.builder()
                .user(userService.getUserEntityFromDataBase(telegramObject))
                .message(telegramObject.getText())
                .senderRole(UserRoleEnum.USER)
                .build();
    }

    public ChatWithAdministratorEntity newMessageFromAdminToUser(TelegramObject telegramObject, UserEntity user) {
        return ChatWithAdministratorEntity.builder()
                .user(user)
                .message(telegramObject.getText())
                .admin(userService.getUserEntityFromDataBase(telegramObject))
                .senderRole(UserRoleEnum.ADMIN)
                .build();
    }
}
