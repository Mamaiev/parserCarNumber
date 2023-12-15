package ua.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.BotConfig;
import ua.db.CarNumberRepository;
import ua.db.ChasingNumberRepository;
import ua.db.SynchronizeRepository;
import ua.model.CarNumber;
import ua.model.Synchronize;
import ua.model.UserRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BotService extends TelegramLongPollingBot {

    private Log log = LogFactory.getLog(ParserSiteService.class);

    private final BotConfig botConfig;
    private ParserSiteService parserSiteService;
    private CarNumberRepository numberRepository;
    private SynchronizeRepository synchronizeRepository;
    private final ChasingNumberRepository chasingNumberRepository;
    private Dispatcher dispatcher;

    public BotService(BotConfig botConfig, ParserSiteService parserSiteService, CarNumberRepository numberRepository, SynchronizeRepository synchronizeRepository, ChasingNumberRepository chasingNumberRepository, Dispatcher dispatcher) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        this.parserSiteService = parserSiteService;
        this.numberRepository = numberRepository;
        this.synchronizeRepository = synchronizeRepository;
        this.chasingNumberRepository = chasingNumberRepository;
        this.dispatcher = dispatcher;
        registerBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String inputText = update.getMessage().getText();
            if (inputText.equals("cu")) {
                try {
                    process();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            UserRequest userRequest = new UserRequest();
            userRequest.setUpdate(update);
            userRequest.setChatId(chatId);
            boolean dispatched = dispatcher.dispatch(userRequest);

            if (!dispatched) {
                log.warn("Unexpected update from user");
            }
        } else if (update.hasCallbackQuery()) {
            try {
                CallbackQuery callback = update.getCallbackQuery();
                Message message = callback.getMessage();
                if (message.getText().equals("Вибери номер який за яким не хочеш більше слідкувати️")) {
                    Long chatId = message.getChatId();
                    String number = callback.getData();
                    chasingNumberRepository.deleteChasingNumberByChatIdAndNumber(chatId, number);
                    sendMessage.setText(callback.getData() + " був видалений.");
                }
                sendMessage.setChatId(message.getChatId());
                execute(sendMessage);
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

    //    @Scheduled(cron = "58 8/11 * * * *") //for prod
//    @Scheduled(cron = "1/1 * * * * *") //for test
    private void process() throws TelegramApiException {
        update();
        checkAvailable();
    }

    private void update() {
        log.info("Scheduler of update is works. ");
        Instant start = Instant.now();
        Synchronize synchronize = new Synchronize();
        synchronize.setSynchronizeTime(LocalDateTime.now());
        try {
            List<CarNumber> listOfNumbers = parserSiteService.pullNumbers();
            numberRepository.findAll();
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

    private void checkAvailable() throws TelegramApiException {
        List<Long> listOfUsers = chasingNumberRepository.findDistinctByUserId();
        log.info("Users: " + listOfUsers);
        List<String> listOfNumbers;
        for (Long userId : listOfUsers) {
            listOfNumbers = chasingNumberRepository.findNumberByUserId(userId);
            log.info("User: " + userId + " numbers: " + listOfNumbers);
            for (String num : listOfNumbers) {
                if (numberRepository.existsByNumber(num)) {
                    log.info("num -> " + num);
                    execute(new SendMessage(String.valueOf(userId), "Number " + num + " is available now."));
                }
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
