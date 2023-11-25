package ua;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.db.CarNumberRepository;
import ua.db.SynchronizeRepository;
import ua.model.CarNumber;
import ua.model.Synchronize;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BotService extends TelegramLongPollingBot {

    private Log log = LogFactory.getLog(ParserSiteService.class);

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
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();
        //TODO: create scheduler for example couple times per day switch on and check update
        if (inputText.equals("sync")) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Hello. Start parse.");  //TODO create 2 stream for sending message and parse. For send 2 diff message
            message.setText("dddddd");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

//    TODO: maybe need do it with some paging, we should pull all date in one query because their could be millions entities
//    @Transactional
//    List<CarNumber> update(List<CarNumber> pulled) {
//        softDeleteCarNumber();
//        return null;
//    }

    //    @Scheduled(cron = "58 8/11 * * *") //for prod
    @Scheduled(cron = "1/1 * * * * *") //for test
    private void update() {
        log.info("Scheduled is work;");
        Instant start = Instant.now();
        Synchronize synchronize = new Synchronize();
        synchronize.setSynchronizeTime(LocalDateTime.now());
        try {
            List<CarNumber> listOfNumbers = parserSiteService.pullNumbers();
            softDeleteCarNumber();
            numberRepository.saveAll(listOfNumbers); // change signature of method
            synchronize.setSuccess(true);
            log.info("Time of processing: " + Duration.between(start, Instant.now()).getSeconds() + " \n"
                    + "Processed " + listOfNumbers.size() + " car numbers");
        } catch (IOException e) {
            synchronize.setSuccess(false);
            e.printStackTrace();
        } finally {
            synchronizeRepository.save(synchronize);
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

    private void softDeleteCarNumber() {
        List<CarNumber> exist = (ArrayList<CarNumber>) numberRepository.findAll();
        if (exist.isEmpty()) {
            return;
        }
        exist.forEach(car -> car.setDeleted(true));
        numberRepository.saveAll(exist);
        log.info("Deleted " + exist.size() + " numbers.");
    }
}
