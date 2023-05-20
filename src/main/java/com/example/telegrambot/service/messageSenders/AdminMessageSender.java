package com.example.telegrambot.service.messageSenders;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserRoles;

import com.example.telegrambot.repository.UserRolesRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminMessageSender extends MessageSender {

    private static final Logger LOGGER = Logger.getLogger(AdminMessageSender.class);

    private List<Long> administratorsIdList;

    @Autowired
    UserRolesRepository userRolesRepository;

    private LocalDateTime lastAdminListUpdateTime;

    public void sendMessageToAllAdmin(String messageText) {
        for (Long adminId : getAdministratorsIdList()) {
            newSendMessage();
            getSendMessage().setChatId(adminId);
            sendMessage(messageText);
            LOGGER.info(String.format("Sent message to admin with chat ID: %s", adminId));
        }
    }

    public void sendMessageToAllAdmin(String text, TelegramObject telegramObject) {
        String messageText = "ОСТАВЛЕН НОВЫЙ ОТЗЫВ!\n\n" + telegramObject.stringFrom() + ": \n\n" + text;
        sendMessageToAllAdmin(messageText);
    }

    /**
     * Получает список идентификаторов пользователей с ролью администратора из репозитория.
     */
    private void fetchAdminUserIds() {
        List<UserRoles> adminRoles = userRolesRepository.findAllByRole("admin");

        administratorsIdList = adminRoles.stream()
                .map(UserRoles::getChatId)
                .collect(Collectors.toList());

        lastAdminListUpdateTime = LocalDateTime.now();
        LOGGER.info("Admin user IDs fetched and list updated");
    }

    /**
     * Возвращает список идентификаторов пользователей с ролью администратора.
     * Если с момента последнего обновления прошел час или более, обновляет список администраторов.
     *
     * @return список идентификаторов пользователей с ролью администратора
     */
    public List<Long> getAdministratorsIdList() {
        if (lastAdminListUpdateTime == null) {
            fetchAdminUserIds();
            LOGGER.info("Admin list fetched for the first time");
            return administratorsIdList;
        }

        Duration duration = Duration.between(lastAdminListUpdateTime, LocalDateTime.now());

        if (duration.toHours() > 0) {
            fetchAdminUserIds();
            LOGGER.info("Admin list updated due to time duration");
        }

        return administratorsIdList;
    }
}