import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Struct;
import java.util.*;

public class RequestResponse{
    private Messages messages;
    private DataBase db;
    private Map<Long,String> map = new HashMap<>();
    private List<String> list = new ArrayList<>();
    private Map<String,List<String>> information = new HashMap<>();//0 - это всегда инфа остальное жанр

    public RequestResponse(Messages messages, DataBase db) {
        this.messages = messages;
        this.db = db;
    }

    public void getInterapt(Update update) throws TelegramApiException, IOException, InterruptedException {

        String message = update.getMessage().getText();
        Long chatID = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();
        if (map.containsKey(chatID)){
            String stat = map.get(chatID);
            switch (stat){
                case "wait_name":
                    db.add(chatID,message);
                    map.remove(chatID);

                    String info = (new ParserInf(message)).contentInformation(list);
                    String text = "\nВы можете также добавить информацию из интернета" +
                            "для улучшения поиска или сохранить только своё название, " +
                            "если найдено не то";
                    messages.sendMessage(chatID,info+text, messages.getInlineKeyboard(new String[][]{
                            {"👴🏻 Добавить с этим описанием", "add_info"},
                            {"👶🏻 Добавить моё название", "add_my"}
                    }));
                    return;
                case "wait_delete":
                    db.remove(chatID,message);
                    map.remove(chatID);
                    messages.sendMessage(chatID,"Удалено: "+ message, messages.getNavigationKeyboard());
                    return;
            }
        }

        switch (message){
            case "/start":
                messages.sendHiMessage(chatID,userName);
                break;
            case "🎞 Выбрать контент":
                choosingContent(chatID);
                break;
            case "📝 Внести изменения":
                coosingEditeMyList(chatID);
                break;
            case "🗂 Мой список":
                String text = (new PrintFilmsList(db)).print(chatID);
                messages.sendMessage(chatID, text, messages.getNavigationKeyboard());
                break;
            case "🦐 Несмешной анекдот":
                messages.sendMessage(chatID, getRandomJoke(), messages.getNavigationKeyboard());
                break;
            default:
                messages.sendMessage(chatID, "😞Нет такой команды (мб пока что)", messages.getNavigationKeyboard());
        }
    }

    public void getCallBack(String callBackData, Update update) throws TelegramApiException {
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        switch (callBackData){
            case "random":
                filmOrSeries(chatID,messageId);
                break;
            case "film":
                choosingGenner(chatID,messageId);
                break;
            case "series":
                choosingGenner(chatID,messageId);
                break;
            case "add_info":
                information.put(list.get(0),list);
                messages.sendMessage(chatID, "Успешно добавлено", messages.getNavigationKeyboard());
                //куда-то сохранить инфу и жанр
                break;
            case "add_my":
                information.remove(list.get(0),list);
                list.clear();
                messages.sendMessage(chatID, "Успешно добавлено", messages.getNavigationKeyboard());
                break;
            case "search":
                messages.sendMessage(chatID, "Введите название", messages.getNavigationKeyboard());
                break;
            case "wish":
                break;
            case "new":
                messages.sendMessage(chatID, "Введите название\n" +
                        "Если вводить на английском можно будет добавить информацию" +
                        " из интернета 😉", messages.getNavigationKeyboard());
                map.put(chatID,"wait_name");
                break;
            case "delete":
                messages.sendMessage(chatID, "Введите название", messages.getNavigationKeyboard());
                map.put(chatID,"wait_delete");
                break;
            case "back_to_ForS":
                filmOrSeries(chatID,messageId);
                break;
            case "back_to_choose":
                editechoosingContent(chatID,messageId);
                break;

        }
    }

    public void choosingGenner(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Выбери жанр, если он имеет значение";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"🎪 Комедия", "comedy", "🎭 Драма", "drama"},
                {"👻 Ужасы", "horror", "😲 Триллер", "triller"},
                {"👽 Фантастика", "fiction", "🎲 Любой", "all"},
                {"👈🏻 Назад", "back_to_ForS"}
        }));
    }

    public void filmOrSeries(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Что именно ты ищешь?";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"📽 Фильм", "film", "📺 Сериал", "series"},
                {"👈🏻 Назад", "back_to_choose"}
        }));
    }

    //нужна чтоб возвращаться
    private void editechoosingContent(Long chatID, Integer messageID) throws TelegramApiException {
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.editMessageKeyboard(chatID,messageID,text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
        }));
    }
    private void choosingContent(Long chatID) throws TelegramApiException {
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.sendMessage(chatID, text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
        }));
    }

    private void coosingEditeMyList(Long chatID) throws TelegramApiException {
        String text = "Что вы хотите отредактировать в вашем списке?";
        messages.sendMessage(chatID, text, messages.getInlineKeyboard(new String[][]{
                {"➕ Добавить контент", "new", "👁 Отметить просмотренное", "already"},
                {"🗑 Удалить", "delete"}
        }));
    }
    private void editeMyList(Long chatID, Integer messageID) throws TelegramApiException {
        String text = "Что вы хотите отредактировать в вашем списке?";
        messages.editMessageKeyboard(chatID, messageID, text, messages.getInlineKeyboard(new String[][]{
                {"➕ Добавить контент", "new", "👁 Отметить просмотренное", "already"},
                {"🗑 Удалить", "delete"}
        }));
    }



    //это оч тупо но я хочу доп кнопку сорри надо будет вынести в отдельный класс или придумать что-то норм
    private String[] jokes = {
            "Почему программисты путают Хэллоуин и Рождество?\nПотому что OCT 31 = DEC 25",

            "Приходит как-то программист в бар. Садится за столик и говорит:\n" +
                    "- Бармен! Мне чаю.\n" +
                    "- Чёрного или зелёного?\n" +
                    "- Любого, всё равно Exception...",

            "Почему Java-разработчики носят очки?\n" +
                    "Потому что они не C#!",

            "Программист звонит в библиотеку:\n" +
                    "- Здравствуйте, Катю можно?\n" +
                    "- Она в архиве.\n" +
                    "- Разархивируйте её пожалуйста!",

            "Сколько программистов нужно, чтобы вкрутить лампочку?\n" +
                    "- Ни одного. Это hardware проблема!",

            "Чат GPT заходит в бар и говорит:\n" +
                    "- Мне самого лучшего пива!\n" +
                    "Бармен:\n" +
                    "- Извините, как разработчик ИИ я не могу рекомендовать алкоголь",

            "Почему телеграм-бот пошёл в лес?\n" +
                    "Чтобы найти новые update!",

            "Бот спрашивает у пользователя:\n" +
                    "- Как тебя зовут?\n" +
                    "- 404\n" +
                    "- Имя не найдено, попробуйте ещё раз"
    };
    private Random random = new Random();
    private String getRandomJoke() {
        return jokes[random.nextInt(jokes.length)];
    }
}
