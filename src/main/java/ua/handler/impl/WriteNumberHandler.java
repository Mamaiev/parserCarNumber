package ua.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.KeyboardHelper;
import ua.db.CarNumberRepository;
import ua.db.ChasingNumberRepository;
import ua.handler.UserRequestHandler;
import ua.model.CarNumber;
import ua.model.ChasingNumber;
import ua.model.UserRequest;
import ua.service.TelegramService;

import java.util.List;

@Service
public class WriteNumberHandler extends UserRequestHandler {

    private Log log = LogFactory.getLog(WriteNumberHandler.class);
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
            if (keyboardHelper.validatePartNumber(text)) {
                return true;
            }
        }
        return false;
    }

    //Check if set number is available. If yes -> info message; if not -> added to db this number(chasing_number)
    @Override
    public void handle(UserRequest request) {
        ReplyKeyboard replyKeyboard = keyboardHelper.chasingNumberButton();
        String text = request.getUpdate().getMessage().getText();
        boolean exist = carNumberRepository.existsByNumberContaining(text);
        if (exist) {
            String string = transliteration(text);
            List<CarNumber> listExistNumber = carNumberRepository.checkNumberByLike(string);
            telegramService.sendMessage(request.getChatId(),
                    listExistNumber.stream().map(s -> s.getNumber() + "\n").toList() + " номер(и) зараз доступний для замовлення. Мерщій в ЕК водія!");
        } else {
            try {
                saveNumber(request);
            } catch (DataIntegrityViolationException e) {
                log.error("For test. Wrote number which contain in db.");
                telegramService.sendMessage(request.getChatId(),
                        "Такий номер вже є в списку очікування.");
            }
            telegramService.sendMessage(request.getChatId(),
                    "Як буде доступним номер бот напише.️");
        }
        telegramService.sendMessage(request.getChatId(),
                "Введи номер(латинецею) який тебе цікавить ⤵️", replyKeyboard);
    }

    @Override
    public int priority() {
        return 1;
    }

    private void saveNumber(UserRequest request) {
        ChasingNumber chasingNumber = new ChasingNumber();
        chasingNumber.setChatId(request.getChatId());
        Message message = request.getUpdate().getMessage();
        chasingNumber.setNumber(transliteration(message.getText()));
        chasingNumber.setUserId(message.getFrom().getId());
        chasingNumberRepository.save(chasingNumber);
    }

    public String transliteration(String ss) {
        StringBuilder word = new StringBuilder();
        char[] array = ss.toUpperCase().toCharArray();
        for (char c : array) {
            word.append(change(c));
        }
        return word.toString();
    }

    private String change(char input) {
        return switch (input) {
            case 'А' -> "A";
            case 'В' -> "B";
            case 'С' -> "C";
            case 'Е' -> "E";
            case 'Н' -> "H";
            case 'І' -> "I";
            case 'К' -> "K";
            case 'М' -> "M";
            case 'О' -> "O";
            case 'Р' -> "P";
            case 'Т' -> "T";
            default -> String.valueOf(input);
        };
    }
}
