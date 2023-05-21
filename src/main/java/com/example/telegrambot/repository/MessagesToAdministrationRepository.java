package com.example.telegrambot.repository;

import com.example.telegrambot.model.MessagesToAdministrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesToAdministrationRepository  extends JpaRepository<MessagesToAdministrationEntity, Integer> {
}
