package org.example.adapter.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramNameRequestMessage {
    private Long chatId;
    private String name;
}
