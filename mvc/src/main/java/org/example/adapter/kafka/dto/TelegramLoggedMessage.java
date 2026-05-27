package org.example.adapter.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TelegramLoggedMessage {
    private Long chatId;
}
