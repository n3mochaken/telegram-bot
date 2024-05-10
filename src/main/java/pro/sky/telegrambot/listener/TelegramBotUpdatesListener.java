package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositoryes.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

//    @Override
//    public int process(List<Update> updates) {
//        updates.forEach(update -> {
//            logger.info("Processing update: {}", update);
//            if (update.message() != null && update.message().text() != null) {
//                if (update.message().text().equals("/start")) {
//                    long chatId = update.message().chat().id();
//                    telegramBot.execute(new SendMessage(chatId, "Привет! Я ТГ бот."));
//                } else {
//                    processIncomingMessage(update.message().chat().id(), update.message().text());
//                }
//            }
//        });
//        return UpdatesListener.CONFIRMED_UPDATES_ALL;
//    }

//    public void processIncomingMessage(Long chatId, String message) {
//        Pattern pattern = Pattern.compile("([0-9.:/\\s]{16})(\\s)([\\W]+)");
//        Matcher matcher = pattern.matcher(message);
//        if (matcher.find()) {
//            String dateTimeString = matcher.group(1);
//            String reminderText = matcher.group(3);
//
//            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
//
//            NotificationTask notificationTask = new NotificationTask();
//            notificationTask.setChatId(chatId);
//            notificationTask.setNotificationDateTime(dateTime);
//            notificationTask.setNotificationText(reminderText);
//
//            notificationTaskRepository.save(notificationTask);
//        } else {
//            System.out.println("cheto ne poshlo");
//        }
//    }
}

