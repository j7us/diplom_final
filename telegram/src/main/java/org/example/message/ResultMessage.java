package org.example.message;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMessage {
    private Long chatId;
    private BigDecimal values;
    private String name;
}
