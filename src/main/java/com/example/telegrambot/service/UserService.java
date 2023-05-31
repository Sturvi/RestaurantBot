package com.example.telegrambot.service;

import com.example.telegrambot.TelegramObject;
import com.example.telegrambot.mapper.UserMapper;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.model.UserStateEnum;
import com.example.telegrambot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public static final int SECONDS_PER_DAY = 86400;

    /**
     * Updates user information in the database.
     * If the user already exists in the database and more than a day has passed since the last update,
     * updates the user information.
     * If the user does not exist in the database, adds them.
     *
     * @param user An instance of User containing information about the user.
     * @return An instance of UserInDataBase containing the updated user information.
     */
    public UserEntity saveOrUpdateUser(User user) {
        log.debug("Updating user info for user with ID: {}", user.getId());

        return userRepository.findByChatId(user.getId()).map(existingUserEntity -> {
            Duration duration = Duration.between(existingUserEntity.getUpdatedAt(), LocalDateTime.now());
            if (duration.toSeconds() > SECONDS_PER_DAY) {
                log.debug("User with ID: {} was last updated more than a day ago, updating...", user.getId());
                updateUserInDataBase(user, existingUserEntity);
            } else {
                log.debug("User with ID: {} was last updated less than a day ago, no update needed", user.getId());
            }
            return existingUserEntity;
        }).orElseGet(() -> {
            log.debug("User with ID: {} not found in database, creating new user...", user.getId());
            return saveUserInDataBase(user);
        });
    }

    /**
     * Saves or updates user information in the database.
     *
     * @param user An instance of User containing information about the user.
     * @return An instance of UserInDataBase containing the updated user information.
     */
    private UserEntity saveUserInDataBase(User user) {
        log.debug("Saving user with ID: {}", user.getId());

        UserEntity userEntity = userMapper.mapNewUserToUserEntity(user);

        userRepository.save(userEntity);

        return userEntity;
    }

    /**
     * Updates an existing user entity with information from a Telegram user.
     *
     * @param user        An instance of User containing information about the user.
     * @param userEntity  An instance of UserEntity containing the existing user information.
     * @return An instance of UserEntity containing the updated user information.
     */
    private UserEntity updateUserInDataBase(User user, UserEntity userEntity){
        userMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        return userEntity;
    }

    /**
     * Changes the user state for the given chat ID to the specified user status.
     *
     * @param newUserState   The new user state.
     * @param telegramObject An instance of TelegramObject containing information about the user.
     */
    public void changeUserState(UserStateEnum newUserState, TelegramObject telegramObject) {
        log.debug("Updating user state for chat ID: {}", telegramObject.getId());

        UserEntity userEntity = userRepository
                .findByChatId(telegramObject.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(telegramObject.getFrom()));

        userMapper.updateUserState(userEntity, newUserState);

        userRepository.save(userEntity);
        log.debug("User state for chat ID: {} successfully updated to {}", telegramObject.getId(), newUserState);
    }

    /**
     * Gets the user state for the given chat ID. If the user does not have a state, sets it to "main".
     *
     * @param telegramObject An instance of TelegramObject containing information about the user.
     * @return The user state.
     */
    public UserStateEnum getUserStatus(TelegramObject telegramObject) {
        UserEntity userEntity = getUserEntityFromDataBase(telegramObject);

        return userEntity.getUserStateEnum();
    }

    public UserEntity getUserEntityFromDataBase (TelegramObject telegramObject) {
        return userRepository
                .findByChatId(telegramObject.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(telegramObject.getFrom()));
    }
}