package com.example.telegrambot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "reviews")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends TimestampedEntity {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private UserInDataBase user;

    @Column(name = "message", nullable = false)
    @NotNull
    private String message;

}

