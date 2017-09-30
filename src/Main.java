import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;


public class Main {

    static String siteURL = "https://www.onthisday.com/day/"; //Use the table view if you change this.


    public static void main(String[] args) {
        try {
            getCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getCards() throws IOException {
        LocalDate start = LocalDate.parse("2017-01-01"),
                end   = LocalDate.parse("2017-12-31");
        LocalDate next = start.minusDays(1);
        String urlToVisit;
        JSONArray cards = new JSONArray();
        while ((next = next.plusDays(1)).isBefore(end.plusDays(1))) {
            System.out.format("Getting questions for day %s of the year\n", next.getDayOfYear());
            urlToVisit = siteURL+next.getMonth()+"/"+next.getDayOfMonth();
            Document doc = Jsoup.connect(urlToVisit).get();
            Elements events = doc.select("ul.event-list").first().select("li.event-list__item");
            Iterator iterator = events.iterator();
            while(iterator.hasNext()) {
                JSONObject card = new JSONObject();
                Element cardInfo = (Element) iterator.next();
                String year = cardInfo.select("a[href~=/events/date/*]").text();
                if (year.matches("^[0-9]{1,4}$")) {
                    String description = cardInfo.text().replace(year, "");
                    card.put("year", year);
                    card.put("description", description);
                    cards.add(card);
                }
            }
        }
        saveCardsToJSON(cards);
    }

    public static void saveCardsToJSON(JSONArray cards) {
        try {
            FileWriter file = new FileWriter("cards.json");
            file.write(cards.toJSONString());
            file.close();
            System.out.println("Created JSON file \"cards.json\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}