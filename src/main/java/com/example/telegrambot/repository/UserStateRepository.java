package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserPhoneNumber;
import com.example.telegrambot.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    Optional<UserState> findByChatId(Long chatId);
}
