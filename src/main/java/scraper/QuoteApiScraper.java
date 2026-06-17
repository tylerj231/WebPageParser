package scraper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.core.Scraper;
import models.ApiResponse;
import models.Quote;
import org.jsoup.Jsoup;
import util.MapperFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuoteApiScraper extends Scraper<Quote> {
    private final ObjectMapper mapper = MapperFactory.getMapper();
    private final String filePath;

    public  QuoteApiScraper(String filePath) {
        this.filePath = filePath;
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
            }

        }
        return quotes;
    }

    @Override
    public void writeJson(List<Quote> quotes) {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, quotes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("Scraping quotes... \uD83D\uDD04");
        List<Quote> quotes = scrape();
        System.out.printf("Found %d \uD83D\uDCDC.\n", quotes.size());
        System.out.printf("Writing to %s...\uD83D\uDCDA\n", filePath);
        writeJson(quotes);
        System.out.println("Success ✅");
    }
}
