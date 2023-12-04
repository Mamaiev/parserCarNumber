package ua.model;

import org.telegram.telegrambots.meta.api.objects.Update;


public class UserRequest {
    private Update update;
    private Long chatId;

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
