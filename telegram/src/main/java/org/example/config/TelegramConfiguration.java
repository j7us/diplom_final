package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfiguration {
    private final TelegramProperties telegramProperties;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(telegramProperties.getToken());
    }
}
