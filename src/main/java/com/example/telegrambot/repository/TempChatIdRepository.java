package com.example.telegrambot.repository;

import com.example.telegrambot.model.TempChatIdEntity;
import com.example.telegrambot.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempChatIdRepository extends JpaRepository<TempChatIdEntity, Integer> {

    Optional<TempChatIdEntity> findByAdminEntity(UserEntity userEntity);
}
