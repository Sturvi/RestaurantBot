package com.example.telegrambot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "chat_with_administrator")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatWithAdministratorEntity extends TimestampedEntity {


    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_chat_id", referencedColumnName = "chat_id")
    private UserEntity user;

    @Column(name = "messageText", nullable = false)
    @NotNull
    private String message;

    @ManyToOne
    @JoinColumn(name = "admin_chat_id", referencedColumnName = "chat_id")
    private UserEntity admin;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role", columnDefinition = "varchar(255) default 'USER'")
    private UserRoleEnum senderRole;
}
