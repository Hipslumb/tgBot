import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Random;

public class ButtomsOut{
    Messages messages;
    public ButtomsOut(Messages messages){
        this.messages = messages;
    }

    public void choosingGenner(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Выбери жанр, если он имеет значение";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"🎪 Комедия", "Comedy", "🎭 Драма", "Drama"},
                {"👻 Ужасы", "Horror", "😲 Триллер", "Thriller"},
                {"👽 Фантастика", "Sci-Fi", "🔪 Криминал", "Crime"},
                {"🕸 Детектив", "Mystery", "🌍 Приключения", "Adventure"},
                {"️🎠 Мультик", "Animation", "💕 Романтика", "Romance"},
                {"🎲 Любой", "all", "👈🏻 Назад", "back_to_ForS"}
        }));
    }

    public void filmOrSeries(Long chatID, Integer messageId) throws TelegramApiException {
        String text = "Что именно ты ищешь?";
        messages.editMessageKeyboard(chatID, messageId, text, messages.getInlineKeyboard(new String[][]{
                {"📽 Фильм", "movie", "📺 Сериал", "series"},
                {"👈🏻 Назад", "back_to_choose"}
        }));
    }

    //нужна чтоб возвращаться
    public void editechoosingContent(Long chatID, Integer messageID) throws TelegramApiException {
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.editMessageKeyboard(chatID,messageID,text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
        }));
    }
    public void choosingContent(Long chatID) throws TelegramApiException {
        String text = "Вы можете выбрать рандомно по жанру или использовать поиск по своему списку";
        messages.sendMessage(chatID, text, messages.getInlineKeyboard(new String[][]{
                {"🎲 Рандомайзер", "random"},
                {"🔎 Поиск", "search"},
        }));
    }

    public void coosingEditeMyList(Long chatID) throws TelegramApiException {
        String text = "Что вы хотите отредактировать в вашем списке?";
        messages.sendMessage(chatID, text, messages.getInlineKeyboard(new String[][]{
                {"➕ Добавить контент", "new", "🗑 Удалить", "delete"}
        }));
    }
    public void editeMyList(Long chatID, Integer messageID) throws TelegramApiException {
        String text = "Что вы хотите отредактировать в вашем списке?";
        messages.editMessageKeyboard(chatID, messageID, text, messages.getInlineKeyboard(new String[][]{
                {"➕ Добавить контент", "new", "🗑 Удалить", "delete"}
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
    public String getRandomJoke() {
        return jokes[random.nextInt(jokes.length)];
    }
}
