package org.example.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import lombok.RequiredArgsConstructor;
import org.example.message.ResultMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultMessageService {
    private final TelegramMessageSender telegramMessageSender;

    public void sendVehicleResult(ResultMessage message) {
        telegramMessageSender.send(
                message.getChatId(),
                "Пробег машины с номером " + message.getName() + " - " + format(message.getValues()));
    }

    public void sendEnterpriseResult(ResultMessage message) {
        telegramMessageSender.send(
                message.getChatId(),
                "Общий пробег предприятия с именем " + message.getName() + " - " + format(message.getValues()));
    }

    private String format(BigDecimal value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);

        return decimalFormat.format(value);
    }
}
