package ua.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.BotConfig;
import ua.db.CarNumberRepository;
import ua.db.SynchronizeRepository;
import ua.model.CarNumber;
import ua.model.Synchronize;
import ua.model.UserRequest;

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
    private Dispatcher dispatcher;

    public BotService(BotConfig botConfig, ParserSiteService parserSiteService, CarNumberRepository numberRepository, SynchronizeRepository synchronizeRepository, Dispatcher dispatcher) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        this.parserSiteService = parserSiteService;
        this.numberRepository = numberRepository;
        this.synchronizeRepository = synchronizeRepository;
        this.dispatcher = dispatcher;
        registerBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String inputText = update.getMessage().getText();
            if (!inputText.isEmpty()) {
//                message.setReplyMarkup(sendInlineKeyBoardMessage());
//                message.setChatId(chatId);
//                message.setText("What do you want?");
//                try {
//                    execute(message);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
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
                message.setText(update.getCallbackQuery().getData());
                message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public InlineKeyboardMarkup sendInlineKeyBoardMessage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Check car number");
        inlineKeyboardButton1.setCallbackData("Please, wrote a car number which you want to check...");
        inlineKeyboardButton2.setText("Button 2");
        inlineKeyboardButton2.setCallbackData("Button 2 has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


//    TODO: maybe need do it with some paging, we should pull all date in one query because their could be millions entities
//    @Transactional
//    List<CarNumber> update(List<CarNumber> pulled) {
//        softDeleteCarNumber();
//        return null;
//    }

    //    @Scheduled(cron = "58 8/11 * * * *") //for prod
//    @Scheduled(cron = "1/1 * * * * *") //for test
    private void process() {
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

    private void checkAvailable() {
        //TODO: go to db pull all number which tracking and check if exist in car_number.
        // if exist -> send message in bot
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
