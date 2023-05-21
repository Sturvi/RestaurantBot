package com.example.telegrambot.service.messagesenders;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserRolesEntity;
import com.example.telegrambot.repository.UserRolesRepository;
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

    private final UserRolesRepository userRolesRepository;

    private LocalDateTime lastAdminListUpdateTime;

    @Autowired
    public AdminMessageSender(TelegramBot telegramBot, UserRolesRepository userRolesRepository) {
        super(telegramBot);
        this.userRolesRepository = userRolesRepository;
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
     * Sends a message with the specified text and TelegramObject to all administrators.
     *
     * @param text           the text of the message
     * @param telegramObject the TelegramObject containing the chat ID
     */
    public void sendMessageToAllAdmin(String text, TelegramObject telegramObject) {
        // todo: имя метода говорит об отправке сообщения всем админам без указания типа сообщения,
        //  а на деле мы отправляем админам уведомление о новом отзыве.
        //  Класс отвечает за отправку сообщений, а тут получается ещё какое-то событие обрабатывается.
        //  Обработку события с новым отзывом лучше вынести в отдельный класс (CustomerEventHandler например)
        //  и текст сообщения генерировать в нём, вызывать отправку сообщения с заданным текстом у класса AdminMessageSender.
        //  Иначе выходит нарушение единой ответственности
        String messageText = "ОСТАВЛЕН НОВЫЙ ОТЗЫВ!\n\n" + telegramObject.stringFrom() + ": \n\n" + text;
        sendMessageToAllAdmin(messageText);
    }

    /**
     * Fetches the list of user IDs with the "admin" role from the repository.
     */
    private void fetchAdminUserIds() {
        List<UserRolesEntity> adminRoles = userRolesRepository.findAllByRole("admin");

        administratorsIdList = adminRoles.stream()
                .map(UserRolesEntity::getChatId)
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
        // todo: ручная реализация кэширования, лучше использовать готовые решения
        //  http://spring-projects.ru/guides/caching/
        //  https://habr.com/ru/articles/465667/
        //  Так же стоит предусмотреть возможность сбрасывания кэша по запросу от админов,
        //  чтобы новый администратор мог начать работать с ботом в день добавления

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