import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileNotFoundException;

// Класс для запуска бота
public class Main {
    public static void main(String[] args) throws FileNotFoundException, TelegramApiException, InterruptedException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        TgBot bot = new TgBot();
        botsApi.registerBot(bot);
        System.out.println("Бот успешно запущен!");

        while (true) {
            Thread.sleep(1000);
        }
    }
}
