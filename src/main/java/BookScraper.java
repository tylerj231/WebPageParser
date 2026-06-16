import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
