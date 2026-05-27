package org.example.adapter.kafka.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TelegramMileageMessage {
    private Long chatId;
    private BigDecimal values;
    private String name;
}
