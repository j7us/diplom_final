package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.UserStatus;
import org.example.repository.UserStatusRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final LoginRequestPublisher loginRequestPublisher;

    public UserStatus getStatus(Long chatId) {
        return userStatusRepository.getStatus(chatId);
    }

    public void waitData(Long chatId) {
        userStatusRepository.saveStatus(chatId, UserStatus.WAITING_DATA);
    }

    public void authenticate(Long chatId, String message) {
        loginRequestPublisher.publish(chatId, message);
    }

    public void markLoggedIn(Long chatId) {
        userStatusRepository.saveStatus(chatId, UserStatus.LOGGED_IN);
    }
}
