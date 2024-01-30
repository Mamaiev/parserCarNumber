package ua.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.KeyboardHelper;
import ua.db.ChasingNumberRepository;
import ua.handler.UserRequestHandler;
import ua.model.ChasingNumber;
import ua.model.UserRequest;
import ua.service.TelegramService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChasingNumberHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final ChasingNumberRepository repository;

    public ChasingNumberHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, ChasingNumberRepository repository) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.repository = repository;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        if (isTextMessage(request.getUpdate())) {
            return isSameTextMessage(request.getUpdate(), "Номера що вже відслідковуються мною") ;
        }
        return false;
    }

    @Override
    public void handle(UserRequest dispatchRequest) {
        List<ChasingNumber> listOfNumber = repository.findByUserId(dispatchRequest.getUpdate().getMessage().getFrom().getId());

        telegramService.sendMessage(dispatchRequest.getChatId(), "This is list of number chasing:\n"
                + listOfNumber.stream().map(s -> s.getNumber() + "\n").collect(Collectors.joining()));
        ReplyKeyboard replyKeyboard = keyboardHelper.chasingNumberButton();
        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Введи номер(латинецею) який тебе цікавить ⤵️", replyKeyboard);

    }

    @Override
    public int priority() {
        return 2;
    }
}
