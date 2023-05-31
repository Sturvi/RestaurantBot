package com.example.telegrambot.service.handler.eventhandlers;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.mapper.ChatWithAdministratorMapper;
import com.example.telegrambot.repository.ChatWithAdministratorRepository;
import com.example.telegrambot.service.AdministratorList;
import com.example.telegrambot.service.Operation;
import com.example.telegrambot.service.handler.Handler;
import com.example.telegrambot.service.messagesenders.AdminMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class ChatEventHandler implements Handler {

    private final AdministratorList administratorList;
    private final ChatWithAdministratorMapper chatWithAdministratorMapper;
    private final ChatWithAdministratorRepository chatWithAdministratorRepository;
    private final AdminMessageSender adminMessageSender;
    private TelegramObject telegramObject;
    private final Map<String, Operation> callbackHandlers = new HashMap<>();

    private void init (TelegramObject telegramObject) {
        this.telegramObject = telegramObject;

    }

    @Override
    public void handle(TelegramObject telegramObject) {
        init(telegramObject);
        log.debug("Handling telegram object with id: {}", telegramObject.getId());

        if (administratorList.hasAdmin(telegramObject.getId())) {
            log.debug("Telegram object is from an admin.");
            handlingUpdateFromAdmin();
        } else {
            log.debug("Telegram object is from a user.");
            handlingUpdateFromUser();
        }
    }

    private void handlingUpdateFromUser() {
        var newChatMessageEntity = chatWithAdministratorMapper.newMessageFromUserToAdmin(telegramObject);
        log.debug("Created new ChatMessageEntity for user.");

        chatWithAdministratorRepository.save(newChatMessageEntity);
        log.info("Saved new ChatMessageEntity to the repository.");

        var messageText = creatNewMessageToAdminText();

        adminMessageSender.prepareAndSendChatMessageToAllAdmins(messageText);
        log.info("Prepared and sent message to all admins.");
    }

    private void handlingUpdateFromAdmin() {
        log.debug("Handling message from admin.");
        // TO-DO: Implement admin message handling
    }

    private String creatNewMessageToAdminText() {
        String firstName = telegramObject.getFrom().getFirstName();
        String lastName = telegramObject.getFrom().getLastName();

        String firstAndLastName = firstName;

        if (lastName != null) {
            firstAndLastName += " " + lastName;
        }

        String message = """
                Поступило новое сообщение от пользователя: %s
                Юзернейм пользователя: %s
                Уникальный идентификатор пользователя: %d
                
                Текст сообщения:\s
                
                %s
                """.formatted(firstAndLastName, telegramObject.getUserName(), telegramObject.getId(), telegramObject.getText());

        log.debug("Created new message to admin: {}", message);
        return message;
    }
}
