package org.example;

import lombok.RequiredArgsConstructor;
import org.example.config.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot {
    private final TelegramProperties telegramProperties;
    private final UpdateConsumer updateConsumer;

    @Override
    public String getBotToken() {
        return telegramProperties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
