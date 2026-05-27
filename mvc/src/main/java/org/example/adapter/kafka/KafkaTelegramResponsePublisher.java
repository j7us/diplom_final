package org.example.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.example.adapter.kafka.dto.TelegramLoggedMessage;
import org.example.adapter.kafka.dto.TelegramMileageMessage;
import org.example.application.client.TelegramResponsePublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTelegramResponsePublisher implements TelegramResponsePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void authorizeChat(Long chatId) {
        send("logged", new TelegramLoggedMessage(chatId));
    }

    @Override
    public void sendEnterpriseMileage(Long chatId, String enterpriseName, BigDecimal mileage) {
        send("enterprise_req", new TelegramMileageMessage(chatId, mileage, enterpriseName));
    }

    @Override
    public void sendVehicleMileage(Long chatId, String vehicleNumber, BigDecimal mileage) {
        send("vehicle_req", new TelegramMileageMessage(chatId, mileage, vehicleNumber));
    }

    private void send(String topic, Object message) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(message));
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось отправить сообщение в Telegram", exception);
        }
    }
}
