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
@Table(name = "messages_to_administration")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MessagesToAdministrationEntity extends TimestampedEntity {


    @ManyToOne
    @NotNull
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id")
    private UserEntity user;

    @Column(name = "user_message", nullable = false)
    @NotNull
    private String userMessage;

    @Column(name = "response")
    @NotNull
    private String response;
}
