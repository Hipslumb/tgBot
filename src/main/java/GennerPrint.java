import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GennerPrint {

    private DataBase db;

    GennerPrint (DataBase db){
        this.db = db;
    }

    public String getContent(Long chatId, String contType, String genner){ // На вход получаю id чата, type - фильм или сериал

        List<String> allMovies = db.getTitles(chatId);

        if (allMovies == null || allMovies.isEmpty()) {
            return "Нет сохраненных фильмов";
        }

        List<String> filtered = new ArrayList<>();

        for (String movie : allMovies){

           List<String> info = db.getInfo(chatId, movie);
           if (info == null) continue;

            String type = findType(info);
            if (type == null || !type.equals(contType)) continue;

            if(!genner.equals("all") && !hasGenner(info, genner)){
                continue;
            }
            filtered.add(movie);
        }

        if (filtered.isEmpty()) {
            return "❌ Не найдено " +  (contType.equals("movie") ? "фильмов" : "сериалов") + " в данном жанре";
        }
        String randomMovie = filtered.get(
                ThreadLocalRandom.current().nextInt(filtered.size())
        );

        return randomMovie;
    }

    private  String findType(List<String> info){

        for(String type : info){
            if("movie".equals(type) || "series".equals(type)){
                return type;
            }
        }
        return null;
    }
    private  boolean hasGenner(List<String> info, String genner){
        for (String gen : info) {
            if (genner.equalsIgnoreCase(gen)) {
                return true;
            }
        }
        return false;
    }
}
