package com.example.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRoles extends TimestampedEntity {

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "role")
    private String role;

}
