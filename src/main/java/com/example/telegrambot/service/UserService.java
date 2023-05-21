package com.example.telegrambot.service;

import com.example.telegrambot.mapper.UserMapper;
import com.example.telegrambot.model.UserEntity;
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

        return userRepository.findByChatId(user.getId()).map(existingUser -> {
            Duration duration = Duration.between(existingUser.getUpdatedAt(), LocalDateTime.now());
            if (duration.toSeconds() > SECONDS_PER_DAY) {
                log.debug("User with ID: {} was last updated more than a day ago, updating...", user.getId());
                return saveOrUpdateUserInDataBase(user);
            } else {
                log.debug("User with ID: {} was last updated less than a day ago, no update needed", user.getId());
                return existingUser;
            }
        }).orElseGet(() -> {
            log.debug("User with ID: {} not found in database, creating new user...", user.getId());
            return saveOrUpdateUserInDataBase(user);
        });
    }

    /**
     * Saves or updates user information in the database.
     *
     * @param user An instance of User containing information about the user.
     * @return An instance of UserInDataBase containing the updated user information.
     */
    private UserEntity saveOrUpdateUserInDataBase(User user) {
        log.debug("Saving user with ID: {}", user.getId());

        UserEntity userEntity = userMapper.mapToUserEntity(user);

        userRepository.save(userEntity);

        return userEntity;
    }
}
