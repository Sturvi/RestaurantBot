package com.example.telegrambot.service.messagesenders;

import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserRoleEnum;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class AdminMessageSender extends MessageSender {

    private List<Long> administratorsIdList;

    private final UserRepository userRepository;

    private LocalDateTime lastAdminListUpdateTime;

    @Autowired
    public AdminMessageSender(TelegramBot telegramBot, UserRepository userRepository) {
        super(telegramBot);
        this.userRepository = userRepository;
    }

    /**
     * Sends a message with the specified text to all administrators.
     *
     * @param messageText the text of the message
     */
    public void sendMessageToAllAdmin(String messageText) {
        for (Long adminId : getAdministratorsIdList()) {
            newSendMessage();
            getSendMessage().setChatId(adminId);
            sendMessage(messageText);
            log.debug("Sent message to admin with chat ID: {}", adminId);
        }
    }

    /**
     * Fetches the list of user IDs with the "admin" role from the repository.
     */
    private void fetchAdminUserIds() {
        List<UserEntity> adminRoles = userRepository.findAllByRole(UserRoleEnum.ADMIN);

        administratorsIdList = adminRoles.stream()
                .map(UserEntity::getChatId)
                .toList();

        lastAdminListUpdateTime = LocalDateTime.now();
        log.debug("Admin user IDs fetched and list updated");
    }

    /**
     * Returns the list of user IDs with the "admin" role. If an hour or more has passed since the last update,
     * updates the list of administrators.
     *
     * @return the list of user IDs with the "admin" role
     */
    public List<Long> getAdministratorsIdList() {

        if (lastAdminListUpdateTime == null) {
            fetchAdminUserIds();
            log.debug("Admin list fetched for the first time");
            return administratorsIdList;
        }

        Duration duration = Duration.between(lastAdminListUpdateTime, LocalDateTime.now());

        if (duration.toHours() > 0) {
            fetchAdminUserIds();
            log.debug("Admin list updated due to time duration");
        }

        return administratorsIdList;
    }
}