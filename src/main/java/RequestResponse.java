import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class RequestResponse{
    protected Messages messages;
    protected DataBase db;
    private Map<Long,String> map = new HashMap<>();
    private Map<Long, List<String>> userLists = new HashMap<>();
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
                    caseWaitName(chatID,message);
                    return;
                case "wait_delete":
                    db.remove(chatID,message);
                    map.remove(chatID);
                    messages.sendMessage(chatID,"Удалено: "+ message, messages.getNavigationKeyboard());
                    return;
                case "wait_search":
                    caseWaitSearch(chatID,message);
                    return;
            }
        }
        ButtomsOut buttomsOut = new ButtomsOut(messages);
        switch (message){

            case "/start":
                messages.sendHiMessage(chatID,userName);
                break;
            case "🎞 Выбрать контент":
                buttomsOut.choosingContent(chatID);
                break;
            case "📝 Внести изменения":
                buttomsOut.coosingEditeMyList(chatID);
                break;
            case "🗂 Мой список":
                String text = (new PrintFilmsList(db)).print(chatID);
                messages.sendMessage(chatID, text, messages.getNavigationKeyboard());
                break;
            case "😝 Несмешной анекдот":
                messages.sendMessage(chatID, buttomsOut.getRandomJoke(), messages.getNavigationKeyboard());
                break;
            default:
                messages.sendMessage(chatID, "😞Нет такой команды", messages.getNavigationKeyboard());
        }
    }

    public void getCallBack(String callBackData, Update update) throws TelegramApiException {
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        ButtomsOut buttomsOut = new ButtomsOut(messages);
        switch (callBackData){
            case "random":
                buttomsOut.filmOrSeries(chatID,messageId);
                break;
            case "film":
                buttomsOut.choosingGenner(chatID,messageId);
                break;
            case "series":
                buttomsOut.choosingGenner(chatID,messageId);
                break;
            case "add_info":
                caseAddInfo(chatID);
                break;
            case "add_my":
                if (userLists.containsKey(chatID)) {
                    userLists.remove(chatID);
                }
                messages.sendMessage(chatID, "Успешно добавлено", messages.getNavigationKeyboard());
                break;
            case "search":
                messages.sendMessage(chatID, "Введите название:", messages.getNavigationKeyboard());
                map.put(chatID,"wait_search");
                break;
            case "wish":
                break;
            case "new":
                messages.sendMessage(chatID, "Введите название:\n" +
                        "Если вводить на английском возможно можно будет добавить информацию" +
                        " из интернета 😉", messages.getNavigationKeyboard());
                map.put(chatID,"wait_name");
                break;
            case "delete":
                messages.sendMessage(chatID, "Введите название", messages.getNavigationKeyboard());
                map.put(chatID,"wait_delete");
                break;
            case "back_to_ForS":
                buttomsOut.filmOrSeries(chatID,messageId);
                break;
            case "back_to_choose":
                buttomsOut.editechoosingContent(chatID,messageId);
                break;

        }
    }

    private void caseWaitName(Long chatID,String message) throws IOException, TelegramApiException, InterruptedException {
        db.addTitleOnly(chatID,message);
        map.remove(chatID);
        List<String> list = new ArrayList<>();

        String info = (new ParserInf(message)).contentInformation(list);

        if (list.isEmpty()) {
            messages.sendMessage(chatID,"\n📝 Контент добавлен с вашим названием.",
                    messages.getNavigationKeyboard());
            return;
        }
        userLists.put(chatID, list);
        String text = "\nВы можете также добавить информацию из интернета " +
                "для улучшения поиска или сохранить только своё название, " +
                "если найдено не то!";
        messages.sendMessage(chatID,info+text, messages.getInlineKeyboard(new String[][]{
                {"👴🏻 Добавить с этим описанием", "add_info"},
                {"👶🏻 Добавить моё название", "add_my"}
        }));
    }
    private void caseWaitSearch(Long chatID,String message) throws TelegramApiException {
        List<String> userFilms = db.getTitles(chatID);
        String foundFilm = null;
        for (String film : userFilms) {
            if (film.equalsIgnoreCase(message)) { // Точное совпадение
                foundFilm = film;
                break;
            }
        }
        if (foundFilm == null) {
            messages.sendMessage(chatID, "❌ Фильм не найден", messages.getInlineKeyboard(new String[][]{
                    {"➕ Добавить контент", "new"}
            }));
        } else {
            List<String> infoArr = db.getInfo(chatID, foundFilm);

            if (infoArr != null && infoArr.size() > 1) {
                String description = infoArr.get(1);
                description = description.replace("###NEWLINE###", "\n");

                String posterUrl = infoArr.get(0);
                boolean hasPoster = posterUrl != null && !posterUrl.isEmpty() &&
                        !posterUrl.equals("N/A") && posterUrl.startsWith("http");
                if (hasPoster) {
                    messages.sendPhoto(chatID, posterUrl, description);
                } else {
                    messages.sendMessage(chatID, description, messages.getNavigationKeyboard());
                }
            } else {
                messages.sendMessage(chatID,
                        "📌 Название:" + foundFilm + "\n",
                        messages.getNavigationKeyboard());
            }
        }
        map.remove(chatID);
    }
    private void caseAddInfo(Long chatID) throws TelegramApiException {
        List<String> list = userLists.get(chatID);

        if (list != null && !list.isEmpty()){
            String title = list.get(0);
            String type = list.get(1);
            String info = list.get(2);

            List<String> infoArr = new ArrayList<>();
            String poster = "";
            if (list.size() > 3) {
                poster = list.get(3);
            }
            infoArr.add(poster);
            infoArr.add(info);
            infoArr.add(type);

            int genreStartIndex = poster.isEmpty() ? 3 : 4;

            for (int i = genreStartIndex; i < Math.min(genreStartIndex + 3, list.size()); i++) {
                infoArr.add(list.get(i));
            }

            db.remove(chatID, title);
            db.updateInfo(chatID, title, infoArr);

            messages.sendMessage(chatID, "Успешно добавлено", messages.getNavigationKeyboard());
            userLists.remove(chatID);
        }
    }
}
