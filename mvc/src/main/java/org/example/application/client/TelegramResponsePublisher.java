package org.example.application.client;

import java.math.BigDecimal;

public interface TelegramResponsePublisher {
    void authorizeChat(Long chatId);

    void sendEnterpriseMileage(Long chatId, String enterpriseName, BigDecimal mileage);

    void sendVehicleMileage(Long chatId, String vehicleNumber, BigDecimal mileage);
}
