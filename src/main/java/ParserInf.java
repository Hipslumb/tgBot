import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParserInf {
    private String name;
    private String URL;

    public ParserInf(String name) throws FileNotFoundException {
        this.name = name;
        this.URL = formURL();
    }

    private String formURL() throws FileNotFoundException {
        File file = new File("D:/parser.txt");
        Scanner scanner = new Scanner(file);
        return scanner.nextLine() + name;
    }

    public String contentInformation(String message, String[] poster) throws IOException, InterruptedException {
        String json = makeHttp();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        if (root.has("Response") && "False".equals(root.get("Response").getAsString())) {
            return root.has("Error") ? root.get("Error").getAsString() : "Неизвестная ошибка API";
        }

        StringBuilder text = new StringBuilder("Добавленный контент был найден:\n");

        String title = getSafeString(root, "Title", "Не указано");
        String type = getSafeString(root, "Type", "Не указано");
        String genre = getSafeString(root, "Genre", "Не указано");
        String rating = getSafeString(root, "imdbRating", "Нет рейтинга");

        text.append("\n📌 Название: ").append(title)
                .append("\n🎞 Тип: ").append(type)
                .append("\n🎨 Жанры: ").append(genre);

        if ("series".equals(type)) {
            String seasons = getSafeString(root, "totalSeasons", "?");
            text.append("\n📊 Сезонов: ").append("N/A".equals(seasons) ? "Нет данных" : seasons);

            String runtime = getSafeString(root, "Runtime", "");
            if (!"N/A".equals(runtime) && !runtime.isEmpty()) {
                text.append("\n⌚ Длительность эпизода: ").append(runtime);
            }
        } else {
            String runtime = getSafeString(root, "Runtime", "?");
            text.append("\n⌚ Длительность: ").append("N/A".equals(runtime) ? "Нет данных" : runtime);
        }

        text.append("\n⭐ Рейтинг: ").append("N/A".equals(rating) ? "Нет рейтинга" : rating);


        String tmpposter = getSafeString(root, "Poster", null);
        if (tmpposter != null && !"N/A".equals(tmpposter)) {
            poster[0] = tmpposter;
        }

        return text.toString();
    }

    private String makeHttp() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("User-Agent", "MyBot/1.0")
                .GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();

    }

    private String getSafeString(JsonObject root, String fieldName, String defaultValue) {
        if (root.has(fieldName)) {
            JsonElement element = root.get(fieldName);
            if (!element.isJsonNull()) {
                String value = element.getAsString();
                return !value.isEmpty() && !"N/A".equals(value) ? value : defaultValue;
            }
        }
        return defaultValue;
    }
}
