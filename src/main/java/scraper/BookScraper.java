package scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.core.Scraper;
import models.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.MapperFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookScraper extends Scraper<Book> {
    private final ObjectMapper mapper = MapperFactory.getMapper();
    private final String filePath;

    public BookScraper(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Book> scrape() {
        String currentUrl = "https://books.toscrape.com/catalogue/category/books/fiction_10/index.html";
        List<Book> books = new ArrayList<>();

        try {
            while (currentUrl != null) {
                Document document = Jsoup.connect(currentUrl).get();

                for (Element article: document.select("article.product_pod")) {
                    String title = article.select("h3 > a").attr("title");
                    double price = Double.parseDouble(article.select("p.price_color").text().substring(1));
                    String availability = article.select("p.availability").text();
                    String ratingClass = article.select("p.star-rating").attr("class");
                    int rating = parseRating(ratingClass.replace("star-rating", "").trim());
                    String detailUrl = article.select("h3 > a").attr("abs:href");

                    Document detailDoc = Jsoup.connect(detailUrl).get();
                    Thread.sleep(300);

                    String upc = detailDoc.select("th:contains(UPC) + td").text();
                    String availText = detailDoc.select("th:contains(Availability) + td").text();
                    int availableQuantity = Integer.parseInt(availText.replaceAll("[^0-9]", ""));

                        Book currentBook = new Book(title, price, availability, availableQuantity, rating, detailUrl, upc);
                        books.add(currentBook);
                    }

                Element next = document.select("li.next > a").first();
                currentUrl = (next != null) ? next.attr("abs:href") : null;
                }

        } catch (IOException e) {
            System.out.println("Oops an error occurred while scraping books. Try again." + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    @Override
    public void writeJson(List<Book> books) {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, books);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        System.out.println("Scraping fiction books... \uD83D\uDD04");
        List<Book> books = scrape();
        System.out.printf("Found %d books.\n", books.size());
        System.out.printf("Writing to %s...\uD83D\uDCDA\n", filePath);
        writeJson(books);
        System.out.println("Success ✅");

    }

    private int parseRating(String rating) {
        return switch (rating) {
            case "One" -> 1;
            case "Two" -> 2;
            case "Three" -> 3;
            case "Four" -> 4;
            case "Five" -> 5;
            default -> 0;
        };
    }
}
