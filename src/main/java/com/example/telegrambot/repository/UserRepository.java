package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByChatId(Long chatId);

    List<UserEntity> findAllByRole(UserRoleEnum role);
}