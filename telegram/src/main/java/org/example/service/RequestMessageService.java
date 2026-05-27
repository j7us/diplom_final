package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestMessageService {
    private static final String ENTERPRISE_WORD = "Предприятие";
    private static final String VEHICLE_WORD = "машина";
    private static final String ENTERPRISE_TOPIC = "enterprise_res";
    private static final String VEHICLE_TOPIC = "vehicle_res";

    private final RequestPublisher requestPublisher;

    public void handle(Long chatId, String message) {
        String[] words = message.trim().split("\\s+", 2);
        String firstWord = words[0];
        String name = words.length > 1 ? words[1] : "";

        if (ENTERPRISE_WORD.equalsIgnoreCase(firstWord)) {
            requestPublisher.publish(ENTERPRISE_TOPIC, chatId, name);
            return;
        }

        if (VEHICLE_WORD.equalsIgnoreCase(firstWord)) {
            requestPublisher.publish(VEHICLE_TOPIC, chatId, name);
        }
    }
}
