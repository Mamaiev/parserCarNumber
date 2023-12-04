package ua.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.KeyboardHelper;
import ua.db.CarNumberRepository;
import ua.db.ChasingNumberRepository;
import ua.handler.UserRequestHandler;
import ua.model.ChasingNumber;
import ua.model.UserRequest;
import ua.service.TelegramService;

@Service
public class WriteNumberHandler extends UserRequestHandler {

    private final KeyboardHelper keyboardHelper;
    private final TelegramService telegramService;
    private final ChasingNumberRepository chasingNumberRepository;
    private final CarNumberRepository carNumberRepository;

    public WriteNumberHandler(KeyboardHelper keyboardHelper, TelegramService telegramService, ChasingNumberRepository chasingNumberRepository, CarNumberRepository carNumberRepository) {
        this.keyboardHelper = keyboardHelper;
        this.telegramService = telegramService;
        this.chasingNumberRepository = chasingNumberRepository;
        this.carNumberRepository = carNumberRepository;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        if (isTextMessage(request.getUpdate()) && !isSameTextMessage(request.getUpdate(), "Номера що вже відслідковуються мною")) {
            String text = request.getUpdate().getMessage().getText();
            if (keyboardHelper.validateNumber(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(UserRequest request) {
        boolean exist = carNumberRepository.existsByNumber(request.getUpdate().getMessage().getText());
        ReplyKeyboard replyKeyboard = keyboardHelper.chasingNumberButton();
        if (exist) {
            telegramService.sendMessage(request.getChatId(),
                    "Цей номер зараз доступний для замовлення. Мерщій в ЕК водія!");
            telegramService.sendMessage(request.getChatId(),
                    "Введи ще номер який тебе цікавить ⤵️", replyKeyboard);
        } else {
            saveNumber(request);
            telegramService.sendMessage(request.getChatId(),
                    "Як буде доступним номер бот напише.️");
            telegramService.sendMessage(request.getChatId(),
                    "Введи ще номер який тебе цікавить ⤵️", replyKeyboard);
        }
    }

    @Override
    public int priority() {
        return 1;
    }

    private void saveNumber(UserRequest request) {
        ChasingNumber chasingNumber = new ChasingNumber();
        chasingNumber.setChatId(request.getChatId());
        chasingNumber.setNumber(request.getUpdate().getMessage().getText());
        chasingNumber.setUserId(request.getUpdate().getMessage().getFrom().getId());
        chasingNumberRepository.save(chasingNumber);
    }
}
