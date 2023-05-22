package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserStateEntity, Integer> {
    Optional<UserStateEntity> findByChatId(Long chatId);
}