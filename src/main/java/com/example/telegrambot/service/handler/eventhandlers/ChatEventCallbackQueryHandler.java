package com.example.telegrambot.service.handler.eventhandlers;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.UserRoleEnum;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.service.Operation;
import com.example.telegrambot.service.UserService;
import com.example.telegrambot.service.handler.Handler;
import com.example.telegrambot.service.messagesenders.UserMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class ChatEventCallbackQueryHandler implements Handler {

    private final UserService userService;
    private final UserMessageSender userMessageSender;

    private TelegramObject telegramObject;
    private final Map<String, Operation> callbackHandle = new HashMap<>();
    private static final Pattern USER_INFO_PATTERN = Pattern.compile("(Поступило новое сообщение от пользователя: .*\\nЮзернейм пользователя: .*\\nУникальный идентификатор пользователя: \\d+)");

    private void init (TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        userMessageSender.setTelegramObject(telegramObject);

        log.debug("Initializing ChatEventCallbackQueryHandler with TelegramObject: {}", telegramObject);

        callbackHandle.put("reply", this::handleReply);
    }

    @Override
    public void handle(TelegramObject telegramObject) {
        init(telegramObject);

        Operation operation = callbackHandle.get(telegramObject.getData());

        if (operation != null) {
            log.debug("Handling telegramObject with operation: {}", operation);
            operation.execute();
        } else {
            log.error("Failed to handle telegramObject, no operation found for: {}", telegramObject.getData());
        }
    }

    private void handleReply() {
        UserRoleEnum userRole = userService.getUserRole(telegramObject);

        log.debug("User role retrieved: {}", userRole);

        String messageText = "Можете отправить ваше сообщение.";

        if (userRole == UserRoleEnum.ADMIN) {
            Optional<String> userInfo = getUserInfoFromMessage();

            if (userInfo.isPresent()) {
                userService.changeUserState(UserStateEnum.ANSWER_TO_MESSAGE_IN_CHAT, telegramObject);
                messageText = userInfo.get() + "\n\n" + messageText;
                userMessageSender.sendMessage(messageText);
                log.debug("Admin reply sent: {}", messageText);
            } else {
                userMessageSender.sendMessage("Во время обработки команды произошла ошибка. " +
                        "Пожалуйста обратитесь к разработчикам.");
                log.warn("Failed to retrieve user info from message for admin reply");
            }
        } else {
            userService.changeUserState(UserStateEnum.ANSWER_TO_MESSAGE_IN_CHAT, telegramObject);
            userMessageSender.sendMessage(messageText);
            log.debug("Reply sent: {}", messageText);
        }
    }

    private Optional<String> getUserInfoFromMessage() {
        String messageText = telegramObject.getText();
        log.debug("Extracting user info from message: {}", messageText);

        Matcher matcher = USER_INFO_PATTERN.matcher(messageText);

        if (matcher.find()) {
            String userInfoString = matcher.group(1);
            log.debug("User info found: {}", userInfoString);
            return Optional.of(userInfoString);
        } else {
            log.error("Unable to find user info in message: {}", messageText);
            return Optional.empty();
        }
    }
}
