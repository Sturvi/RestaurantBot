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
@Table(name = "temp_chat_id")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TempChatIdEntity extends TimestampedEntity{

    @OneToOne
    @NotNull
    @JoinColumn(name = "admin_chat_id", referencedColumnName = "chat_id")
    private UserEntity adminEntity;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_chat_id", referencedColumnName = "chat_id")
    private UserEntity recipientUserEntity;
}
