package com.example.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users_phone_number")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserPhoneNumber extends TimestampedEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "phone_number")
    private String phoneNumber;

}
