package scraper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.core.Scraper;
import models.ApiResponse;
import models.Quote;
import org.jsoup.Jsoup;
import util.MapperFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuoteApiScraper extends Scraper<Quote> {
    private final ObjectMapper mapper = MapperFactory.getMapper();

    public QuoteApiScraper(String filePath) {
        super(filePath);
    }

    @Override
    public List<Quote> scrape() {
        List<Quote> quotes = new ArrayList<>();
        int page = 1;
        boolean hasNext = true;

        while(hasNext) {
            String url = "https://quotes.toscrape.com/api/quotes?page=%d".formatted(page);
            try {
                String json = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .execute()
                        .body();
                ApiResponse<Quote> mappedResponse = mapper.readValue(json, new TypeReference<>() {
                });
                quotes.addAll(mappedResponse.getQuotes());
                page++;
                hasNext = mappedResponse.isHasNext();

            } catch (IOException e) {
                System.out.println("Oops! An error occurred while scraping. Please try again");
                break;
            }

        }
        return quotes;
    }

    @Override
    public String resourceName() {
        return "quotes";
    }

}
