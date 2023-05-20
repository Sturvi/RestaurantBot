package com.example.telegrambot.repository;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    Optional<UserState> findByChatId(Long chatId);

     Logger log = LoggerFactory.getLogger(UserStateRepository.class);

    default String changeUserState(String userStatus, TelegramObject telegramObject) {
        log.debug("Обновление статуса пользователя с chatId: {}", telegramObject.getId());
        UserState userState = this.findByChatId(telegramObject.getId()).orElseGet(() -> {
            UserState newUserState = new UserState();
            newUserState.setChatId(telegramObject.getId());
            return newUserState;
        });

        userState.setUserState(userStatus);
        this.save(userState);
        log.debug("Статус пользователя с chatId: {} успешно обновлен на {}", telegramObject.getId(), userStatus);

        return userStatus;
    }
}