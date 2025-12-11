import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;

public class PrintFilmsList {

    private DataBase db;

    PrintFilmsList(DataBase db){
        this.db = db;
    }

    public String print (Long chatId) throws TelegramApiException {

        List<String> filmList = db.get(chatId);
        String text = "";
        if (filmList == null || filmList.isEmpty()) {
            text =  "У вас нет сохраненных фильмов";
            return text;
        }
        String message = "";
        for (int index = 0; index < filmList.size(); index++) {
            message = message + (index + 1) + ". " + filmList.get(index) + "\n";
        }
        text = "🎬 Ваш список:\n" + message;
        return text;
    }
}
