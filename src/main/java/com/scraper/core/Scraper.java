package com.scraper.core;

import util.MapperFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class Scraper<T> {
    protected final String filePath;
    public abstract List<T> scrape();
    public abstract String resourceName();

    public Scraper(String filePath) {
        this.filePath = filePath;
    }

    public void run() {
            System.out.printf("Scraping %s \uD83D\uDD04...\n", resourceName());
            List<T> result = scrape();
            System.out.printf("Found \uD83D\uDD0E %d elements \n", result.size());
            System.out.printf("Writing to %s \uD83D\uDD04...\n", filePath);
            writeJson(result);
            System.out.println("Success ✅");
        }

    public void writeJson(List<T> result) {
            try {
                File file = new File(filePath);
                MapperFactory
                        .getMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(file, result);

            } catch (IOException e) {
                throw new RuntimeException("Failed to write JSON to " + filePath, e);
            }
        }
}
