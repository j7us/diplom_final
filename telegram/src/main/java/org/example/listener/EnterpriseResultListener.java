package org.example.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.message.ResultMessage;
import org.example.service.ResultMessageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterpriseResultListener {
    private final ObjectMapper objectMapper;
    private final ResultMessageService resultMessageService;

    @KafkaListener(topics = "enterprise_req")
    public void listen(String message) {
        try {
            ResultMessage resultMessage = objectMapper.readValue(message, ResultMessage.class);
            resultMessageService.sendEnterpriseResult(resultMessage);
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось обработать сообщение предприятия", exception);
        }
    }
}
