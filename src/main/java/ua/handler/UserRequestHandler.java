package ua.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ua.model.UserRequest;

public abstract class UserRequestHandler {
    // check in which handler need forward request
    public abstract boolean isApplicable(UserRequest request);

    public abstract void handle(UserRequest dispatchRequest);

    // set priority order of handlers. 0 it is first, 1 is second ...
    public abstract int priority();

    public boolean isCommand(Update update, String command) {
        return update.hasMessage() && update.getMessage().isCommand()
                && update.getMessage().getText().equals(command);
    }

    public boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean isSameTextMessage(Update update, String text) {
        return update.getMessage().getText().equals(text);
    }

}
