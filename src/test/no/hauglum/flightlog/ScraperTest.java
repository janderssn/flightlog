package no.hauglum.flightlog;

import no.hauglum.flightlog.domain.FlightDay;
import no.hauglum.flightlog.domain.Pilot;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ScraperTest {

    private Scraper mScraper;

    @Before
    public void setUp() {
        mScraper = new Scraper();
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

        Document doc = mScraper.scrape("https://no.flightlog.org/fl.html?l=2&country_id=160&start_id=6111&a=42");
        Elements tables = doc.select("table");
        int noOfTables = tables.size();
        Element table = tables.get(noOfTables-1);
        Elements rows = table.select("tr");
        List<Pilot> persons = new ArrayList<>();
        List<FlightDay> days = new ArrayList<>();

        rows.stream().forEach(row -> {
            if(mScraper.isADayRow(row)) {
                System.out.print("**** Found new day ***  " + row.text());
                days.add(new FlightDay(row.text()));
            }
            Elements cells = row.select("td");
            cells.stream().forEach(c -> {
                System.out.print(c.text());
            });
            System.out.println("");
        });
    }

    @Test
    public void canDetectDayHeader() {
        Element element = mock(Element.class);
        when(element.text()).thenReturn("2018-04-13");
        assertTrue(mScraper.isADayRow(element));
        when(element.text()).thenReturn("2018-04-13 hubba");
        assertFalse(mScraper.isADayRow(element));
        when(element.text()).thenReturn("hubba 2018-04-13");
        assertFalse(mScraper.isADayRow(element));
        when(element.text()).thenReturn("hubba");
        assertFalse(mScraper.isADayRow(element));
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