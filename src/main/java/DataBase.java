
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

            data.clear();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    int eqIndex = line.indexOf("=[");
                    int lastBracket = line.lastIndexOf("]");

                    if (eqIndex == -1 || lastBracket == -1) continue;

                    try {
                        Long chatId = Long.parseLong(line.substring(0, eqIndex).trim());
                        String content = line.substring(eqIndex + 2, lastBracket);

                        String[] parts = content.split("###", 2);
                        if (parts.length < 2) continue;

                        String movie = parts[0];
                        String allInfo = parts[1];

                        List<String> info = new ArrayList<>();
                        String[] infoParts = allInfo.split("###");
                        for (String part : infoParts) {
                            // ФИКС: возвращаем переносы строк обратно
                            info.add(restoreNewlines(part));
                        }

                        Map<String, List<String>> userData = data.computeIfAbsent(
                                chatId, k -> new HashMap<>()
                        );
                        userData.put(movie, info);

                    } catch (Exception e) {
                        System.out.println("Ошибка в строке: " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка загрузки: " + e.getMessage());
            }
        }


        // Сохранение базы данных в "D:/database.txt"
        private void saveToFile() {
            try (PrintWriter writer = new PrintWriter(filename)) {
                for (Map.Entry<Long, Map<String, List<String>>> userEntry : data.entrySet()) {
                    Long chatId = userEntry.getKey();
                    Map<String, List<String>> userData = userEntry.getValue();

                    for (Map.Entry<String, List<String>> filmEntry : userData.entrySet()) {
                        String movie = filmEntry.getKey();
                        List<String> info = filmEntry.getValue();

                        StringBuilder sb = new StringBuilder();
                        sb.append(chatId).append("=[");
                        sb.append(movie).append("###");

                        for (int i = 0; i < info.size(); i++) {
                            // ФИКС: экранируем переносы строк
                            String escapedItem = escapeNewlines(info.get(i));
                            sb.append(escapedItem);
                            if (i < info.size() - 1) {
                                sb.append("###");
                            }
                        }
                        sb.append("]");

                        writer.println(sb.toString());
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
                return null;
            }
            List<String> info = userData.get(movie);
            if (info == null || info.isEmpty()) {
                return null;
            }
            return new ArrayList<>(info);
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
                userData.put(movie, new ArrayList<>());
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
            else {
                add(chatId, movie, info);
            }
        }

        // Заменяем \n на метку перед сохранением
        private String escapeNewlines(String text) {
            if (text == null) return "";
            return text.replace("\n", "%%NL%%");
        }

        // Возвращаем \n обратно при загрузке
        private String restoreNewlines(String text) {
            if (text == null) return "";
            return text.replace("%%NL%%", "\n");
        }
    }
