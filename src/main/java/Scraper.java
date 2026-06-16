import java.util.List;

public abstract class Scraper<T> {
    public abstract List<T> scrape();
    public abstract void writeJson(List<T> objects);
}
