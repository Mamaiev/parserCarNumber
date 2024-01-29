package ua.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.KeyboardHelper;
import ua.db.CarNumberRepository;
import ua.db.SynchronizeRepository;
import ua.handler.UserRequestHandler;
import ua.model.UserRequest;
import ua.service.TelegramService;

import java.time.LocalDateTime;

@Service
public class InfoSynchronizationHandler extends UserRequestHandler {

    private final KeyboardHelper keyboardHelper;
    private final CarNumberRepository carNumberRepository;
    private final SynchronizeRepository synchronizeRepository;
    private final TelegramService telegramService;

    public InfoSynchronizationHandler(KeyboardHelper keyboardHelper, CarNumberRepository carNumberRepository, SynchronizeRepository synchronizeRepository, TelegramService telegramService) {
        this.keyboardHelper = keyboardHelper;
        this.carNumberRepository = carNumberRepository;
        this.synchronizeRepository = synchronizeRepository;
        this.telegramService = telegramService;
    }


    @Override
    public boolean isApplicable(UserRequest request) {
        if (isTextMessage(request.getUpdate()) && isSameTextMessage(request.getUpdate(), "Статус синхронізації")) {
            return true;
        }
        return false;
    }

    @Override
    public void handle(UserRequest dispatchRequest) {
        LocalDateTime synchronizeTime = synchronizeRepository.findLastSuccessBySynchronizeTime();
        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Last success sync:\n" + synchronizeTime);

        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Всього записів в бд: " + carNumberRepository.size());

        ReplyKeyboard replyKeyboard = keyboardHelper.chasingNumberButton();
        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Введи номер який тебе цікавить ⤵️", replyKeyboard);
    }

    @Override
    public int priority() {
        return 4;
    }
}
