package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.IncorrectMessageException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositoryes.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.util.CommandConstants.*;

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

    @Override
    public int process(List<Update> updates) throws IncorrectMessageException {

        updates.forEach(update -> {

            logger.info("Апдейт запущен, данные: {}", update);


                if (update.message() != null && update.message().text() != null) {
                    String messageText = update.message().text();
                    long chatId = update.message().chat().id();

                    switch (messageText){
                        case START_CMD:
                            logger.info(START_CMD + " " + LocalDateTime.now());
                        telegramBot.execute(new SendMessage(chatId, WELCOME + update.message().from().username() + "!"));
                        telegramBot.execute(new SendMessage(chatId, HELP_MSG));
                        break;
                        case HELP_CMD:
                            logger.info(HELP_MSG + " " + LocalDateTime.now());
                            telegramBot.execute(new SendMessage(chatId, HELP_MSG));
                            break;
                        default:
                            try {
                                parseIncomingMessage(update.message().chat().id(), messageText);
                        } catch (IncorrectMessageException e) {
                            telegramBot.execute(new SendMessage(chatId, INVALID_MSG));
                        }
                    }

                }

        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void parseIncomingMessage(Long chatId, String message)  throws IncorrectMessageException {
        logger.info("Запущен метод преобразования сообщения.");

        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)"); //([0-9\.\:\s]{16})(\s)([\W+]+) //([0-9.:/\s]{16})(\s)([\W]+)
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String dateTimeString = matcher.group(1);
            String reminderText = matcher.group(3);

            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setChatId(chatId);
            notificationTask.setNotificationDateTime(dateTime);
            notificationTask.setNotificationText(reminderText);
            telegramBot.execute(new SendMessage(chatId, "Ваше напоминание \"" + reminderText + "\" принято! Пришлю Вам это сообщение в указанное время - " + dateTime));
            logger.info("Метод преобразования сообщения закончил работу.");

            notificationTaskRepository.save(notificationTask);
        } else {
            logger.info("Пользователем ведена неверная дата.");
            throw new IncorrectMessageException("Введена неверная дата.");
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        logger.info("Запущен процесс происка отложенного напоминания.");

        LocalDateTime currentMinute = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        List<NotificationTask> tasksToSend = notificationTaskRepository.findByNotificationDateTime(currentMinute);

        for (NotificationTask task : tasksToSend) {
            long chatId = task.getChatId();
            String messageText = task.getNotificationText();
            telegramBot.execute(new SendMessage(chatId, messageText));
            logger.info("Напоминание отправлено в чат ID {}: {}", chatId, messageText);
        }
        logger.info("Процес проверки отложенных сообщений заверщен.");
    }
}

