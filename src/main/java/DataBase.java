import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.util.*;

public class DataBase {

    private Map<Long, List<String>> data = new HashMap<>();
    private String filename = "D:/database.txt";

    public DataBase() {
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            File file = new File(filename);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    try {
                        Long key = Long.parseLong(parts[0].trim());
                        String[] movies = parts[1].split(",");
                        List<String> list = new ArrayList<>(Arrays.asList(movies));
                        data.put(key, list);
                    } catch (NumberFormatException e) {
                        System.out.println("Пропущена строка с некорректным ключом: " + parts[0]);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    // Сохранение базы данных в "D:/database.txt"
    private void saveToFile() {
        try {
            PrintWriter writer = new PrintWriter(filename);
            for (Map.Entry<Long, List<String>> entry : data.entrySet()) {
                String movies = String.join(",", entry.getValue());
                writer.println(entry.getKey() + "=" + movies);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    // Добавить фильм (ЧАТ ID, Название фильма)
    public void add(Long key, String movie) {

        List<String> list = data.computeIfAbsent(key, k -> new ArrayList<>());

        if (!list.contains(movie)) {
            list.add(movie);
            saveToFile();
        }
    }

    // Получить конкретный фильм (ЧАТ ID, Название фильма)
    public String get(Long key, int index) {
        List<String> list = data.get(key);
        if (list != null && index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    // Получить список фильмов (ЧАТ ID)
    public List<String> get(Long key) {
        return data.getOrDefault(key, new ArrayList<>());
    }

    // Удаление пользователя (ЧАТ ID)
    public void delete(Long key) {
        data.remove(key);
        saveToFile();
    }

    // Удаление конкретного фильма (ЧАТ ID, Название фильма)
    public void remove(Long key, String movie) {
        List<String> list = data.get(key);
        if (list != null) {
            list.remove(movie);
            if (list.isEmpty()) {
                data.remove(key);
            }
        }
        saveToFile();
    }
}
