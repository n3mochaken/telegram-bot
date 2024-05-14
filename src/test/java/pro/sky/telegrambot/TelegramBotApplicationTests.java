package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import pro.sky.telegrambot.exceptions.IncorrectMessageException;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositoryes.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelegramBotApplicationTests {

	@Autowired
	NotificationTaskRepository notificationTaskRepository;
	@SpyBean
	TelegramBot telegramBot;
	@SpyBean
	TelegramBotUpdatesListener out;
	@Captor
	ArgumentCaptor<SendMessage> messageArgumentCaptor;
	@LocalServerPort
	private int serverPort;

	@Test
	void contextLoads() {
	}

	@Test
	void should_parse_message_and_save_task() throws IncorrectMessageException {
		// Устанавливаем ожидаемые значения
		String message = "16.05.2024 22:22 String notificationText";
		LocalDateTime expectedDateTime = LocalDateTime.parse("16.05.2024 22:22", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		String expectedNotificationText = "String notificationText";

		// Вызываем метод, который должен сохранить задачу
		out.parseIncomingMessage(1L, message);

		// Проверяем, что задача была сохранена в репозитории
		assertTrue(notificationTaskRepository.findAll().stream()
				.anyMatch(task -> task.getNotificationText().equals(expectedNotificationText)
						&& task.getNotificationDateTime().equals(expectedDateTime)));
	}
}


