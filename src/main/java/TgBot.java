import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TgBot extends TelegramLongPollingBot {
    private String botName = "YM_MediaKeeperBot";
    private String token;
    private RequestResponse request;

    public TgBot() throws FileNotFoundException {
        File file = new File("D:/token.txt");
        Scanner scanner = new Scanner(file);
        this.token = scanner.nextLine();
        scanner.close();
        this.request = new RequestResponse(new Messages(this));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
    @Override
    public String getBotToken(){
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            try {
                request.getInterapt(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else if (update.hasCallbackQuery()){
            try {
                request.getCallBack(update.getCallbackQuery().getData(),update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
