package com.example.telegrambot.service.handler.eventhandlers;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.model.ChatWithAdministratorEntity;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserRoleEnum;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.repository.ChatWithAdministratorRepository;
import com.example.telegrambot.service.AdministratorList;
import com.example.telegrambot.service.Operation;
import com.example.telegrambot.service.UserService;
import com.example.telegrambot.service.exceptions.ChatIdNotFoundException;
import com.example.telegrambot.service.handler.Handler;
import com.example.telegrambot.service.messages.messagesenders.UserMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
    private final ChatWithAdministratorRepository chatWithAdministratorRepository;
    private final AdministratorList administratorList;

    private TelegramObject telegramObject;
    private final Map<String, Operation> callbackHandle = new HashMap<>();
    private static final Pattern USER_INFO_PATTERN = Pattern.compile("(Поступило новое сообщение от пользователя: .*\\nЮзернейм пользователя: .*\\nУникальный идентификатор пользователя: \\d+)");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("Уникальный идентификатор пользователя: (\\d+)");


    private void init(TelegramObject telegramObject) {
        this.telegramObject = telegramObject;
        userMessageSender.setTelegramObject(telegramObject);

        log.debug("Initializing ChatEventCallbackQueryHandler with TelegramObject: {}", telegramObject);

        callbackHandle.put("reply", this::handleReply);
        callbackHandle.put("history", this::handleHistory);
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
            Optional<String> userInfo = extractInfoFromMessage(USER_INFO_PATTERN);

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

    private void handleHistory() {
        UserRoleEnum userRole = userService.getUserRole(telegramObject);

        log.debug("User role retrieved: {}", userRole);

        Long ChatIdForSearchInDataBase;

        try {
            ChatIdForSearchInDataBase = getChatIdForSearchInDataBase(userRole);
        } catch (ChatIdNotFoundException e) {
            log.error("...");
            userMessageSender.sendMessage("Произошла непредвиденная ошибка. Мы работаем над исправлением. Попробуйте повторить попытку чуть позже");
            return;
        }

        var chatHistory = getChatHistory(ChatIdForSearchInDataBase);

        String chatHistoryString = convertChatHistoryListToString(chatHistory);
    }

    private Optional<String> extractInfoFromMessage(Pattern pattern) {
        String messageText = telegramObject.getText();
        log.debug("Extracting info from message: {}", messageText);

        Matcher matcher = pattern.matcher(messageText);

        if (matcher.find()) {
            String extractedInfo = matcher.group(1);
            log.debug("Info found: {}", extractedInfo);
            return Optional.of(extractedInfo);
        } else {
            log.error("Unable to find info in message: {}", messageText);
            return Optional.empty();
        }
    }

    public List<ChatWithAdministratorEntity> getChatHistory(Long chatId) {
        List<ChatWithAdministratorEntity> chatHistory = chatWithAdministratorRepository.findAll();

        return chatHistory.stream()
                .filter(chat -> chat.getUser().getChatId().equals(chatId))
                .sorted((chat1, chat2) -> chat2.getCreatedAt().compareTo(chat1.getCreatedAt()))
                .toList();
    }

    private Long getChatIdForSearchInDataBase(UserRoleEnum userRole) throws ChatIdNotFoundException {
        if (userRole == UserRoleEnum.USER) {
            return telegramObject.getId();
        } else {
            var chatIdOpt = extractInfoFromMessage(USER_ID_PATTERN);

            if (chatIdOpt.isPresent()) {
                return Long.parseLong(chatIdOpt.get());
            } else {
                throw new ChatIdNotFoundException();
            }
        }
    }

    private String convertChatHistoryListToString(List<ChatWithAdministratorEntity> chatHistory) {
        var chatHistoryStringBuilder = new StringBuilder();

        for (ChatWithAdministratorEntity chatEntity : chatHistory) {
            var senderInfo = getSenderInfo(chatEntity);

            if (messageLengthIsValid(chatHistoryStringBuilder, senderInfo)) {
                chatHistoryStringBuilder.insert(0, senderInfo);
            } else {
                log.debug("Chat history message length exceeded limit. Truncating history.");
                break;
            }
        }

        log.debug("Chat history converted to string successfully.");
        return chatHistoryStringBuilder.toString();
    }

    private StringBuilder getSenderInfo(ChatWithAdministratorEntity chatEntity) {
        var senderInfo = new StringBuilder();

        senderInfo.append(chatEntity.getCreatedAt()).append(" ");

        if (chatEntity.getSenderRole() == UserRoleEnum.USER) {
            appendUserInfo(senderInfo, chatEntity.getUser());
        } else if (chatEntity.getSenderRole() == UserRoleEnum.ADMIN) {
            appendAdminInfo(senderInfo, chatEntity.getAdmin());
        }

        senderInfo.append(chatEntity.getMessage());
        log.debug("Sender info created: {}", senderInfo);

        return senderInfo;
    }

    private void appendUserInfo(StringBuilder senderInfo, UserEntity user) {
        senderInfo.append(user.getFirstName());

        if (user.getLastName() != null) {
            senderInfo.append(" ").append(user.getLastName());
        }

        senderInfo.append(" ").append(user.getUsername()).append(":\n");
        log.debug("User info appended to sender info: {}", senderInfo);
    }

    private void appendAdminInfo(StringBuilder senderInfo, UserEntity admin) {
        senderInfo.append("Администратор ");
        if (administratorList.hasAdmin(telegramObject.getId())){
            senderInfo.append(admin.getFirstName());

            if (admin.getLastName() != null) {
                senderInfo.append(" ").append(admin.getLastName());
            }

            senderInfo.append(" ").append(admin.getUsername());
        }
        senderInfo.append(":\n");
        log.debug("Admin info appended to sender info: {}", senderInfo);
    }

    private boolean messageLengthIsValid(StringBuilder chatHistory, StringBuilder newMessage) {
        boolean isValid = chatHistory.length() + newMessage.length() <= 4000;
        if (!isValid) {
            log.warn("Message length with new message would exceed limit.");
        }
        return isValid;
    }
}
