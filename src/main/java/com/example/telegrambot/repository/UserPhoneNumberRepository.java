package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhoneNumberRepository extends JpaRepository<UserPhoneNumber, Long> {
    Optional<UserPhoneNumber> findByChatId(Long chatId);
}
