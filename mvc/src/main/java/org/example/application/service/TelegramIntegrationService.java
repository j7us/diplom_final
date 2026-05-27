package org.example.application.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.example.application.client.TelegramResponsePublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramIntegrationService {
    private final TelegramResponsePublisher telegramResponsePublisher;

    public void processLogin(Long chatId, String message) {
        telegramResponsePublisher.authorizeChat(chatId);
    }

    public void processEnterpriseMileageRequest(Long chatId, String enterpriseName) {
        telegramResponsePublisher.sendEnterpriseMileage(chatId, enterpriseName, new BigDecimal("17898.20"));
    }

    public void processVehicleMileageRequest(Long chatId, String vehicleNumber) {
        telegramResponsePublisher.sendVehicleMileage(chatId, vehicleNumber, new BigDecimal("25.51"));
    }
}
