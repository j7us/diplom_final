package org.example.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.model.UserStatus;
import org.springframework.stereotype.Repository;

@Repository
public class UserStatusRepository {
    private final Map<Long, UserStatus> statusesByChatId = new ConcurrentHashMap<>();

    public UserStatus getStatus(Long chatId) {
        return statusesByChatId.getOrDefault(chatId, UserStatus.NOT_LOGGED_IN);
    }

    public void saveStatus(Long chatId, UserStatus status) {
        statusesByChatId.put(chatId, status);
    }
}
