package org.example.adapter.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramLoginRequestMessage {
    private Long chatId;
    private String message;
}
