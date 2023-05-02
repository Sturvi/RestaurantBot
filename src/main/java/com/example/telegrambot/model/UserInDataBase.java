package com.example.telegrambot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInDataBase {

    @Id
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "first_contact_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime firstContact;

    @Column(name = "last_contact_date")
    private LocalDateTime lastContact;

    @Column(name = "user_status")
    private boolean userStatus;

    @Column(name = "position")
    private String position;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getFirstContact() {
        return firstContact;
    }

    public void setFirstContact(LocalDateTime firstContact) {
        this.firstContact = firstContact;
    }

    public LocalDateTime getLastContact() {
        return lastContact;
    }

    public void setLastContact(LocalDateTime lastContact) {
        this.lastContact = lastContact;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public String isPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
