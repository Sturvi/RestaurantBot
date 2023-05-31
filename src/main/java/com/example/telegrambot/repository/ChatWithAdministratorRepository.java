package com.example.telegrambot.repository;

import com.example.telegrambot.model.ChatWithAdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatWithAdministratorRepository  extends JpaRepository<ChatWithAdministratorEntity, Integer> {
}
