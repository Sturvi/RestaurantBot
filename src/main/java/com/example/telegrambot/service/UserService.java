package com.example.telegrambot.service;

import com.example.telegrambot.model.UserInDataBase;
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

    /**
     * Updates user information in the database.
     * If the user already exists in the database and more than a day has passed since the last update,
     * updates the user information.
     * If the user does not exist in the database, adds them.
     *
     * @param user An instance of User containing information about the user.
     * @return An instance of UserInDataBase containing the updated user information.
     */
    // todo: Нейминг. Метод возвращает активного пользователя, либо обновляет, либо создаёт нового.
    //       А имя говорит о том, что метод просто обновляет пользователя.
    public UserInDataBase updateUserInfo(User user) {
        log.debug("Updating user info for user with ID: {}", user.getId());

        return userRepository.findByChatId(user.getId()).map(existingUser -> {
            Duration duration = Duration.between(existingUser.getUpdatedAt(), LocalDateTime.now());
            if (duration.toDays() >= 1) {
                // todo: duration.toDays() что покажет, если в нём будет посчитано секунд на 1.9999 дня?
                //  Если 1 - то потенциально может возникнуть баг, лучше считать количество дней по секундам в сутках.
                //  Если duration.toSeconds() > SECONDS_PER_DAY
                log.debug("User with ID: {} was last updated more than a day ago, updating...", user.getId());
                // todo: метод не только обновляет пользователя, но и создаёт нового, если его нет в бд.
                //  Лучше переименовать в saveOrUpdateUser
                return updateUser(user);
            } else {
                log.debug("User with ID: {} was last updated less than a day ago, no update needed", user.getId());
                return existingUser;
            }
        }).orElseGet(() -> {
            log.debug("User with ID: {} not found in database, creating new user...", user.getId());
            return updateUser(user);
        });
    }

    /**
     * Saves or updates user information in the database.
     *
     * @param user An instance of User containing information about the user.
     * @return An instance of UserInDataBase containing the updated user information.
     */
    private UserInDataBase updateUser(User user) {
        log.debug("Saving user with ID: {}", user.getId());

        // todo: функциональность маппера, лучше вынести в отдельный класс UserMapper и вызывать его метод для получения
        //  сущности из телеграмовского объекта.
        UserInDataBase userInDataBase = UserInDataBase.builder()
                .chatId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .userStatus(true)
                .build();

        userRepository.save(userInDataBase);

        return userInDataBase;
    }
}
