import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

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
}
