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
// todo: нейминг. UserStateEntity
// todo: Такое ощущение, что пользователь может иметь только 1 стейт.
//  Если это так, то лучше перетащить его в сущность пользователя.
public class UserState extends TimestampedEntity {
    @Column(name = "chat_id", unique = true)
    private Long chatId;

    // todo: Какие стейты может иметь пользователь? Это конечное множество значений?
    //  Если да, то лучше использовать енам вместо строки. Тогда замаппить можно будет так
    //
    //  @Enumerated(EnumType.STRING)
    //  private UserState userState;
    //
    //  https://thorben-janssen.com/hibernate-enum-mappings/
    @Column(name = "user_state")
    private String userState;
}
