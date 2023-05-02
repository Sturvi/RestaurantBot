package com.example.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserState {

    @Id
    private Long chatId;

    @Column(name = "user_state")
    private String userState;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserStatus(String userStatus) {
        this.userState = userStatus;
    }

}
