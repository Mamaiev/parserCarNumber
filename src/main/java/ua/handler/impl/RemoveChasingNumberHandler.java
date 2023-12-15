package ua.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.KeyboardHelper;
import ua.db.ChasingNumberRepository;
import ua.handler.UserRequestHandler;
import ua.model.ChasingNumber;
import ua.model.UserRequest;
import ua.service.TelegramService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RemoveChasingNumberHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final ChasingNumberRepository repository;

    public RemoveChasingNumberHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, ChasingNumberRepository repository) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.repository = repository;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && isSameTextMessage(request.getUpdate(), "Видалити номер з відслідковуваних");
    }

    @Override
    public void handle(UserRequest dispatchRequest) {
        List<ChasingNumber> listOfNumber = repository.findByUserId(dispatchRequest.getUpdate().getMessage().getFrom().getId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(Collections.singletonList(listOfNumber
                .stream()
                .map(n -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(n.getNumber());
                    button.setCallbackData(n.getNumber());
                    return button;
                })
                .collect(Collectors.toList())));

        telegramService.sendMessage(dispatchRequest.getChatId(),
                "Вибери номер який за яким не хочеш більше слідкувати️", markup);

    }

    @Override
    public int priority() {
        return 3;
    }
}
