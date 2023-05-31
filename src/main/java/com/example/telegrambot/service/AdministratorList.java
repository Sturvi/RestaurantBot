package com.example.telegrambot.service;

import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserRoleEnum;
import com.example.telegrambot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdministratorList {
    private List<Long> administratorsIdList;
    private final UserRepository userRepository;
    private LocalDateTime lastAdminListUpdateTime;

    /**
     * Fetches the list of user IDs with the "admin" role from the repository.
     * Updates the lastAdminListUpdateTime and logs the debug message.
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
     * Returns the list of user IDs with the "admin" role.
     * If an hour or more has passed since the last update, updates the list of administrators.
     *
     * @return the list of user IDs with the "admin" role
     */
    public List<Long> getAdministratorsIdList() {
        updateAdminListIfNeeded();
        return administratorsIdList;
    }

    /**
     * Checks if the provided chat ID belongs to an administrator.
     * If an hour or more has passed since the last update, updates the list of administrators.
     *
     * @param chatId the chat ID to check
     * @return true if the chat ID belongs to an administrator, false otherwise
     */
    public boolean hasAdmin(Long chatId) {
        updateAdminListIfNeeded();
        boolean isAdmin = administratorsIdList.contains(chatId);
        if (isAdmin) {
            log.debug("Chat ID {} belongs to an administrator", chatId);
        } else {
            log.debug("Chat ID {} does not belong to an administrator", chatId);
        }
        return isAdmin;
    }

    /**
     * Checks if an hour or more has passed since the last update,
     * and if so, fetches the list of administrators and logs the debug message.
     */
    private void updateAdminListIfNeeded() {
        if (lastAdminListUpdateTime == null || hasPassedOneHour()) {
            fetchAdminUserIds();
            log.debug("Admin list updated");
        }
    }

    /**
     * Checks if an hour or more has passed since the last update.
     *
     * @return true if an hour or more has passed since the last update, false otherwise
     */
    private boolean hasPassedOneHour() {
        Duration duration = Duration.between(lastAdminListUpdateTime, LocalDateTime.now());
        boolean hasPassedOneHour = duration.toHours() > 0;
        log.debug("Has passed one hour since last update: {}", hasPassedOneHour);
        return hasPassedOneHour;
    }
}
