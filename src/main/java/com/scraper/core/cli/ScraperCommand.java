package com.scraper.core.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import scraper.BookScraper;
import scraper.QuoteApiScraper;
import scraper.QuoteJsScraper;

@Command(
     name = "run",
     description = "runs scraping logic for all resources."

)
public class ScraperCommand implements Runnable {
    public static void main(String[] args) {
        CommandLine.run(new ScraperCommand(), args);
    }

    @Override
    public void run() {
        new QuoteJsScraper("output/quotes-js.json").run();
        new QuoteApiScraper("output/quotes.json").run();
        new BookScraper("output/books.json").run();
    }
}
