package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserInDataBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserInDataBase, Long> {
}
