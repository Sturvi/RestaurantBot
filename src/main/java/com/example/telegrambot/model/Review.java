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
// todo: имя сущности - ReviewEntity
public class Review extends TimestampedEntity {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id")
    // todo: Рекоммендация - установи JPA-BUDDY, он упрощает создание сущностей
    //       https://jpa-buddy.com/documentation/#installation-and-project-setup
    private UserInDataBase user;

    @Column(name = "message", nullable = false)
    @NotNull
    private String message;

}

