package com.example.telegrambot.repository;

import com.example.telegrambot.model.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRolesEntity, Integer> {
    List<UserRolesEntity> findAllByRole(String role);
}
