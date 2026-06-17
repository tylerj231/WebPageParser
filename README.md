# WebPageParser

A Java Web Page Parses with a CLI tool for scraping book and quote data from [books.toscrape.com](https://books.toscrape.com) and [quotes.toscrape.com](https://quotes.toscrape.com).

## Features

- Scrapes books with pagination from `books.toscrape.com`, including detail-page fields (UPC, stock quantity)
- Scrapes quotes from the infinite-scroll API at `quotes.toscrape.com/scroll`
- Scrapes quotes embedded in inline JavaScript at `quotes.toscrape.com/js`
- Outputs results as structured JSON files
- Runs as a CLI command via Picocli

## Tech Stack

- **Java 17**
- **Jsoup**
- **Jackson**
- **Lombok**
- **Picocli**
- **Maven**

## Project Structure

```
src/main/java/com/scraper/
├── core/
│   ├── cli/
│   │   └── ScraperCommand.java
│   ├── Main.java
│   └── Scraper.java
├── models/
│   ├── ApiResponse.java
│   ├── Author.java
│   ├── Book.java
│   └── Quote.java
├── scraper/
│   ├── BookScraper.java
│   ├── QuoteApiScraper.java
│   └── QuoteJsScraper.java
└── util/
    └── MapperFactory.java
```

## Prerequisites

- Java 17 or later
- Maven 3.8+

Check your versions:

```bash
java -version
mvn -version
```

## Build

From the project root:

```bash
mvn clean package
```

This produces two JAR files in `target/`:

| File | Description |
|---|---|
| `WebPageParses-1.0-SNAPSHOT.jar` | Thin JAR — your code only |
| `WebPageParses-1.0-SNAPSHOT-jar-with-dependencies.jar` | Fat JAR — includes all dependencies (Jsoup, Jackson, Picocli) |

Run the **fat JAR** — the thin one will throw `NoClassDefFoundError` since it doesn't bundle dependencies.

## Run

```bash
java -jar target/WebPageParser-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This runs all three scrapers in sequence and writes their output to the `output/` directory:

```
output/
├── books.json
├── quotes.json
└── quotes-js.json
```

If your `pom.xml` doesn't have the main class set in the manifest, run it explicitly instead:

```bash
java -cp target/WebPageParses-1.0-SNAPSHOT-jar-with-dependencies.jar com.scraper.core.cli.ScraperCommand
```

## Output

Each scraper writes pretty-printed JSON. Example book entry:

```json
{
  "title": "Sharp Objects",
  "price": 47.82,
  "availability": "In stock",
  "availableQuantity": 20,
  "rating": 4,
  "detailUrl": "https://books.toscrape.com/catalogue/sharp-objects_997/index.html",
  "upc": "e00eb4fd7b871a48"
}
```

Example quote entry:

```json
{
  "text": "The world as we have created it is a process of our thinking.",
  "author": {
    "name": "Albert Einstein"
  },
  "tags": ["change", "deep-thoughts", "thinking", "world"]
}
```

## Notes

- A small delay is added between detail-page requests in `BookScraper` to avoid overloading the server.
- All scraping is done via plain HTTP requests (Jsoup for HTML, Jackson for JSON) — no browser is launched at any point.
