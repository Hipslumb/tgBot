import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Random;

public class RequestResponse{
    private Messages messages;
    public RequestResponse(Messages messages) {
        this.messages = messages;
    }

    public void getInterapt(Update update) throws TelegramApiException {

        String message = update.getMessage().getText();
        Long chatID = update.getMessage().getChatId();
        Integer messageID = update.getMessage().getMessageId();
        String userName = update.getMessage().getFrom().getFirstName();

        switch (message){
            case "/start":
                messages.sendHiMessage(chatID,userName);
                break;
            case "🎞 Выбрать контент":
                choosingContent(chatID);
                break;
            case "📝 Внести изменения":
                //удачиииии
                break;
            case "🗂 Мой список":
                //делай делай Ярик, я хочу отображение списком с кнопочками
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
            case "search":
                messages.sendMessage(chatID, "Введите название", messages.getNavigationKeyboard());
                break;
            case "back1":
                filmOrSeries(chatID,messageId);
                break;
            case "back2":
                editechoosingContent(chatID,messageId);
                break;
            case "back3":

                break;
        }
    }

    public void choosingGenner(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Выбери жанр, если он имеет значение";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"🎪 Комедия", "comedy", "🎭 Драма", "drama"},
                {"👻 Ужасы", "horror", "😲 Триллер", "triller"},
                {"👽 Фантастика", "fiction", "🎲 Любой", "all"},
                {"👈🏻 Назад", "back1"}
        }));
    }

    public void filmOrSeries(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Что именно ты ищешь?";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"📽 Фильм", "film", "📺 Сериал", "series"},
                {"👈🏻 Назад", "back2"}
        }));
    }

    private void editechoosingContent(Long chatID, Integer messageID) throws TelegramApiException {
        //Long chatID = update.getMessage().getChatId();
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.editMessageKeyboard(chatID,messageID,text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
        }));
    }

    private void choosingContent(Long chatID) throws TelegramApiException {
        //Long chatID = update.getMessage().getChatId();
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.sendMessage(chatID, text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
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
