package com.example.telegrambot.mapper;

import com.example.telegrambot.model.TempChatIdEntity;
import com.example.telegrambot.model.UserEntity;
import com.example.telegrambot.repository.TempChatIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempChatIdMapper {
    private final TempChatIdRepository tempChatIdRepository;

    /**
     * Adds or updates a temporary recipient chat ID for the given admin and user entities.
     *
     * @param adminEntity the admin entity
     * @param userEntity  the user entity
     */
    public void addOrUpdateTempRecipientChatId(UserEntity adminEntity, UserEntity userEntity) {
        log.debug("Adding or updating temporary recipient chat ID for admin {} and user {}", adminEntity, userEntity);
        Optional<TempChatIdEntity> existingRecord = tempChatIdRepository.findByAdminEntity(adminEntity);

        try {
            if (existingRecord.isPresent()) {
                log.debug("Updating existing temporary recipient chat ID");
                TempChatIdEntity tempChatIdEntity = existingRecord.get();
                tempChatIdEntity.setRecipientUserEntity(userEntity);

                log.debug(tempChatIdEntity.toString());
                tempChatIdRepository.save(tempChatIdEntity);
            } else {
                log.debug("Adding new temporary recipient chat ID");
                TempChatIdEntity tempChatIdEntity = TempChatIdEntity
                        .builder()
                        .adminEntity(adminEntity)
                        .recipientUserEntity(userEntity)
                        .build();
                log.debug(tempChatIdEntity.toString());
                tempChatIdRepository.save(tempChatIdEntity);
            }
        } catch (Exception e) {
            log.error(e.toString() + "\n ");
        }
    }
}
