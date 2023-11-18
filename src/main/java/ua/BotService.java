package ua;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class BotService extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private ParserSiteService parserSiteService;
    private CarNumberRepository numberRepository;
    private SynchronizeRepository synchronizeRepository;

    public BotService(BotConfig botConfig, ParserSiteService parserSiteService, CarNumberRepository numberRepository, SynchronizeRepository synchronizeRepository) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        this.parserSiteService = parserSiteService;
        this.numberRepository = numberRepository;
        this.synchronizeRepository = synchronizeRepository;
        registerBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        LocalDateTime timeOfProcessing = LocalDateTime.now();
        Instant start = Instant.now();
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        if (inputText.equals("sync")) {
            Synchronize synchronize = new Synchronize();
            synchronize.setSynchronizeTime(LocalDateTime.now());
            SendMessage message = new SendMessage();
            try {
                message.setChatId(chatId);
//                message.setText("Hello. Start parse.");  //TODO create 2 stream for sending message and parse. For send 2 diff message

                numberRepository.saveAll(parserSiteService.pullNumbers()); // change signature of method
                synchronize.setSuccess(true);
                message.setText("Time of processing: " + Duration.between(start, Instant.now()).getSeconds());
                execute(message);
            } catch (TelegramApiException | IOException e) {
                synchronize.setSuccess(false);
                e.printStackTrace();
            } finally {
                synchronizeRepository.save(synchronize);

            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
