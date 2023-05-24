package com.example.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserEntity extends TimestampedEntity{

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "user_status")
    private boolean userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", columnDefinition = "varchar(255) default 'MAIN'")
    private UserStateEnum userStateEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255) default 'USER'")
    private UserRoleEnum role;

}
