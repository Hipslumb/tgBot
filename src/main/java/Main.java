import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileNotFoundException;

// Класс для запуска бота
public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            DataBase db = new DataBase();
            TgBot bot = new TgBot(db);
            botsApi.registerBot(bot);
            System.out.println("Бот успешно запущен!");

            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e){
            System.out.println("Operation interruped " + e.getMessage());
        }
        catch (TelegramApiException e){
            System.out.println("TelegramApi " + e.getMessage());
        }
        catch (FileNotFoundException e){
            System.out.println("File error " + e.getMessage());
        }
        catch (RuntimeException e){
            System.out.println("Runtime error " + e.getMessage());
        }
    }
}
