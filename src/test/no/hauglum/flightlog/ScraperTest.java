package no.hauglum.flightlog;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ScraperTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void scrape() {
        Scraper scraper = new Scraper();
        scraper.scrape("https://spring.io/blog");
    }

    @Test
    public void scrapeFlightlog() {
        Scraper scraper = new Scraper();
        Document doc = scraper.scrape("https://no.flightlog.org/fl.html?l=2&country_id=160&start_id=6111&a=42");
        Elements select = doc.select("a");
    }

    @Test
    public void badUrl() {
        Scraper scraper = new Scraper();
        exception.expect(FatalException.class);
        scraper.scrape("https://spring.io.io.io.io.io/blog");
    }

    @Test
    public void badPage() {
        Scraper scraper = new Scraper();
        exception.expect(FatalException.class);
        scraper.scrape("https://spring.io/HUBBABUBBA");
    }
}