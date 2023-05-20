package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserInDataBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInDataBase, Long> {
    Optional<UserInDataBase> findByChatId(Long chatId);
}