package scraper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.core.Scraper;
import models.ApiResponse;
import models.Quote;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.MapperFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuoteJsScraper extends Scraper<Quote> {
    private final ObjectMapper mapper = MapperFactory.getMapper();

    public QuoteJsScraper(String filePath) {
        super(filePath);
    }

    @Override
    public List<Quote> scrape() {
            String url = "https://quotes.toscrape.com/js/";
            List<Quote> quotes = new ArrayList<>();

            try {
                Document doc = Jsoup.connect(url)
                        .timeout(10_000)
                        .get();
                Element scriptTag = doc.select("script:containsData(var data)").first();

                if (scriptTag == null) {
                    System.out.println("Could find data script tag.");
                    return List.of();
                }

                String scriptContent = scriptTag.data();

                int start = scriptContent.indexOf("[");
                int end = scriptContent.indexOf("];");
                String jsonArray = (scriptContent.substring(start, end) + "]").trim();
                String wrappedJson = "{\"quotes\": " + jsonArray + "}";
                ApiResponse<Quote> response = mapper.readValue(wrappedJson, new TypeReference<>() {});
                quotes.addAll(response.getQuotes());

            } catch (IOException e) {
                System.out.println("Ooops. An error has occurred while parsing.");
            }
            return quotes;
    }

    @Override
    public String resourceName() {
        return "quotes";
    }

}
