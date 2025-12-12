    import org.telegram.telegrambots.meta.api.objects.Update;

    import java.io.*;
    import java.util.*;

    public class DataBase {

        private Map<Long, Map <String, List<String>>> data = new HashMap<>();
        private String filename = "D:/database.txt";

        public DataBase() {
            loadFromFile();
        }

        private void loadFromFile() {
            File file = new File(filename);
            if (!file.exists()) {
                data = new HashMap<>();
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":", 3);
                    if (parts.length == 3) {
                        try {
                            Long key = Long.parseLong(parts[0].trim());
                            String movie = parts[1].trim();

                            String[] infoArray = parts[2].split(",");
                            List<String> info = new ArrayList<>(Arrays.asList(infoArray));

                            Map<String, List<String>> list = data.computeIfAbsent(
                                    key, k -> new HashMap<>()
                            );
                            list.put(movie, info);
                        } catch (NumberFormatException e) {
                            System.out.println("Пропущена строка с некорректным ключом: " + parts[0]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка загрузки: " + e.getMessage());
                data = new HashMap<>();
            }
        }

        // Сохранение базы данных в "D:/database.txt"
        private void saveToFile() {
            try (PrintWriter writer = new PrintWriter(filename)){
                for (Map.Entry<Long, Map<String, List<String>>> userEntry : data.entrySet()) {
                    Long chatId = userEntry.getKey();
                    Map<String, List<String>> userData = userEntry.getValue();

                    for (Map.Entry<String, List<String>> filmEntry : userData.entrySet()) {
                        String filmTitle = filmEntry.getKey();
                        List<String> info = filmEntry.getValue();

                        if (info != null && info.size() == 1 && "NO_INFO".equals(info.get(0))) {
                            continue;
                        }

                        String infoString = String.join(",", info);
                        writer.println(chatId + ":" + filmTitle + ":" + infoString);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка сохранения: " + e.getMessage());
            }
        }

        // Добавить фильм (ЧАТ ID, Название фильма, [постер, описание, тип, жанры])
        public void add(Long chatId, String movie, List<String> info) {

            Map<String, List<String>> userData = data.computeIfAbsent(chatId, k -> new HashMap<>());
            userData.put(movie, new ArrayList<>(info));
            saveToFile();
        }

        // Получить конкретный фильм (ЧАТ ID, Название фильма)
        public List<String> getInfo(Long key, String movie) {
            Map<String, List<String>> userData = data.get(key);
            if (userData == null || !userData.containsKey(movie)) {
                return null; // или вернуть пустой список
            }
            return new ArrayList<>(userData.get(movie));
        }

        // Получить список фильмов (ЧАТ ID)
        public List<String> getTitles(Long key) {
            Map<String, List<String>> userData = data.get(key);
            if (userData == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(userData.keySet());
        }

        // Удаление пользователя (ЧАТ ID)
        public void delete(Long key) {
            data.remove(key);
            saveToFile();
        }

        // Удаление конкретного фильма (ЧАТ ID, Название фильма)
        public void remove(Long key, String movie) {
            Map<String, List<String>> userData = data.get(key);
            if (userData != null) {
                userData.remove(movie);
                if (userData.isEmpty()) {
                    data.remove(key);
                }
                saveToFile();
            }
        }

        //Добавить только название
        public void addTitleOnly(Long chatId, String movie) {
            Map<String, List<String>> userData = data.computeIfAbsent(chatId, k -> new HashMap<>());

            if (!userData.containsKey(movie)) {
                userData.put(movie, Arrays.asList("NO_INFO")); // Пустой список информации
                saveToFile();
                System.out.println("+file (title only)");
            }
        }

        //Обновить информацию о фильме
        public void updateInfo(Long chatId, String movie, List<String> info) {
            Map<String, List<String>> userData = data.get(chatId);
            if (userData != null && userData.containsKey(movie)) {
                userData.put(movie, new ArrayList<>(info));
                saveToFile();
                System.out.println("+file (info updated)");
            }
        }
    }
