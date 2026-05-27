package org.example;

import lombok.RequiredArgsConstructor;
import org.example.model.UserStatus;
import org.example.service.RequestMessageService;
import org.example.service.TelegramMessageSender;
import org.example.service.UserStatusService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private static final String LOGIN_COMMAND = "/login";
    private static final String LOGIN_ALREADY_DONE_MESSAGE = "логин уже выполнен";
    private static final String ENTER_LOGIN_AND_PASSWORD_MESSAGE = "введите логин и пароль";

    private final TelegramMessageSender telegramMessageSender;
    private final UserStatusService userStatusService;
    private final RequestMessageService requestMessageService;

    @Override
    public void consume(Update update) {
        if (!isTextMessage(update)) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        UserStatus status = userStatusService.getStatus(chatId);

        if (LOGIN_COMMAND.equals(text)) {
            handleLogin(chatId, status);
            return;
        }

        if (UserStatus.WAITING_DATA.equals(status)) {
            userStatusService.authenticate(chatId, text);
            return;
        }

        requestMessageService.handle(chatId, text);
    }

    private boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private void handleLogin(Long chatId, UserStatus status) {
        if (UserStatus.LOGGED_IN.equals(status)) {
            telegramMessageSender.send(chatId, LOGIN_ALREADY_DONE_MESSAGE);
            return;
        }

        userStatusService.waitData(chatId);
        telegramMessageSender.send(chatId, ENTER_LOGIN_AND_PASSWORD_MESSAGE);
    }
}
