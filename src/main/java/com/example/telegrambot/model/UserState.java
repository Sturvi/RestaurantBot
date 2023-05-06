package com.example.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_state")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserState extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "user_state")
    private String userState;
}
