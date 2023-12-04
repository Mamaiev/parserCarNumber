package ua;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;

@Service
public class KeyboardHelper {

    public ReplyKeyboardMarkup chasingNumberButton() {
//        KeyboardRow row1 = Arrays
//                .stream(Region.values())
//                .map(s -> new KeyboardButton(s.name()))
//                .collect(Collectors.toCollection(KeyboardRow::new));

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("Номера що вже відслідковуються мною");

        row2.add(button2);

        return ReplyKeyboardMarkup.builder()
                .keyboard(Collections.singleton(row2))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildMainMenu() {
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRow.add("❗️Потрібна допомога");

        return ReplyKeyboardMarkup.builder()
//                .selective(false)
//                .resizeKeyboard(true)
//                .oneTimeKeyboard(false)
                .clearKeyboard()
                .build();
    }

    public ReplyKeyboardMarkup buildMenuWithCancel() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("BTN_CANCEL");

        return ReplyKeyboardMarkup.builder()
                .keyboard(Collections.singletonList((keyboardRow)))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public boolean validateNumber(String number) {
        if (number.length() == 8) {
            if (number.substring(0, 2).matches("\\D{2}")
                    && number.substring(2, 6).matches("\\d{4}")
                    && number.substring(6, 8).matches("\\D{2}")) {
                return true;
            }
        }
        return false;
    }
}
