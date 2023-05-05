package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserRoles;
import com.example.telegrambot.repository.AllRepository;

import org.apache.log4j.Logger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMessageSender extends MessageSender {

    private static final Logger LOGGER = Logger.getLogger(AdminMessageSender.class);

    private AllRepository allRepository;
    private List<Long> administratorsIdList;

    private LocalDateTime lastAdminListUpdateTime;

    private TelegramObject telegramObject;

    /**
     * Конструктор AdminMessageSender принимает объект AllRepository и TelegramObject и инициализирует список администраторов.
     *
     * @param allRepository  объект, содержащий доступ к репозиториям
     * @param telegramObject объект для работы с текстом сообщений
     */
    public AdminMessageSender(AllRepository allRepository, TelegramObject telegramObject) {
        super();
        this.allRepository = allRepository;
        administratorsIdList = new ArrayList<>();
        this.telegramObject = telegramObject;
        LOGGER.info("AdminMessageSender initialized");
    }

    /**
     * Отправляет сообщение всем администраторам с указанным текстом.
     *
     * @param text текст сообщения
     */
    public void sendMessageToAllAdmin(String text) {
        String messageText = "ОСТАВЛЕН НОВЫЙ ОТЗЫВ!\n\n" + telegramObject.stringFrom() + ": \n\n" + text;

        for (Long adminId : getAdministratorsIdList()) {
            newSendMessage();
            getSendMessage().setChatId(adminId);
            sendMessage(messageText);
            LOGGER.info(String.format("Sent message to admin with chat ID: %s", adminId));
        }
    }

    /**
     * Получает список идентификаторов пользователей с ролью администратора из репозитория.
     */
    private void fetchAdminUserIds() {
        List<UserRoles> adminRoles = allRepository.getUserRolesRepository().findAllByRole("admin");

        administratorsIdList = adminRoles.stream()
                .map(UserRoles::getId)
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