package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserPhoneNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhoneNumberRepository extends JpaRepository<UserPhoneNumberEntity, Integer> {
    Optional<UserPhoneNumberEntity> findByChatId(Long chatId);
}
