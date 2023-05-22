package com.example.telegrambot.repository;

import com.example.telegrambot.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {


}
