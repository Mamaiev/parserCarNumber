package ua.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.KeyboardHelper;
import ua.handler.UserRequestHandler;
import ua.model.UserRequest;
import ua.service.TelegramService;

@Service
public class ErrorWritingHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;


    public ErrorWritingHandler(TelegramService telegramService, KeyboardHelper keyboardHelper) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && !keyboardHelper.validateNumber(request.getUpdate().getMessage().getText());
    }

    @Override
    public void handle(UserRequest dispatchRequest) {
        ReplyKeyboard replyKeyboard = keyboardHelper.chasingNumberButton();
        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Введений формат номеру неправильний. Формат:\n" +
                        "АА7777АА де АА - будь яка латинська буква\n" +
                        "7777 - будь які чотири цифри крім 0000", replyKeyboard);
    }

    @Override
    public int priority() {
        return 3;
    }

}
