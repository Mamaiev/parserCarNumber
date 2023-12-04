package ua;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Service
public class VolunteerHelpBotSender extends DefaultAbsSender {

    protected VolunteerHelpBotSender() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return "6452246245:AAH0yib0RbRh7dnNUP1PjG-URekyYKZcNOM";
    }
}
