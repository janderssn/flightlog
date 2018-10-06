package no.hauglum.flightlog;

import no.hauglum.flightlog.domain.DayPass;
import no.hauglum.flightlog.domain.FlightDay;
import no.hauglum.flightlog.domain.Pilot;
import org.hamcrest.core.IsEqual;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.*;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertFalse;
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
        HashMap<String, Pilot> pilots = new HashMap<String, Pilot>();
        List<FlightDay> days = new ArrayList<>();
        HashMap<String, DayPass> dayPasses = new HashMap<String, DayPass>();

        FlightDay flightDay = null;
        for (Element row : rows) {
            if (mScraper.isADayRow(row)) {
                out.print("**** Found new day ***  " + row.text());
                flightDay = new FlightDay(row.text());
                days.add(flightDay);
            } else if (mScraper.isAFlightRow(row)) {
                Elements cells = row.select("td");
                for (int i = 0; i < cells.size(); i++) {
                    Element cell = cells.get(i);
                    switch (i) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            break;
                        }
                        case 2: {
                            Elements links = cell.select("a");
                            Element firstLink = links.get(0);
                            String userId = mScraper.parseUserId(firstLink);
                            String name = mScraper.parseName(firstLink);

                            Pilot pilot = new Pilot(userId, name);
                            pilots.put(userId, pilot);

                            DayPass dayPass = new DayPass(pilot, flightDay);
                            dayPasses.put(flightDay.getDate() + "-" + pilot.getUserId(), dayPass);

                            break;
                        }
                        case 3: {
                            break;
                        }
                        case 4: {
                            break;
                        }
                        case 5: {
                            break;
                        }
                    }
                }
                cells.stream().forEach(c -> {
                    out.print(c.text());
                });
            } else {
                out.println("some other row");
            }
            out.println("");
        }

        out.println("Antall flydager: " + days.size());
        out.println("Antall unike piloter: " +pilots.size());
        out.println("Antall dagspass" +dayPasses.size());
    }

    @Test
    public void canParseUserId() {
        Element element = new Element("hello");
        element.attr("href", "asdkføaksjndøfjanø user_id=1234 sdjfnøajsnødj");
        assertTrue("User id is not parsed","1234".equals(mScraper.parseUserId(element)));
    }

    @Test
    public void canParseUserId2() {
        Element element = new Element("hello");
        element.attr("href", "asdkføaksjndøfjanø user_id=1234");
        assertTrue("User id is not parsed","1234".equals(mScraper.parseUserId(element)));
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
    public void canDetectLogRow() {
        Element row = mock(Element.class);
        when(row.select("td")).thenReturn(getSomeElemets(5));
        assertFalse(mScraper.isAFlightRow(row));

        when(row.select("td")).thenReturn(getSomeElemets(6));
        assertTrue(mScraper.isAFlightRow(row));

        when(row.select("td")).thenReturn(getSomeElemets(7));
        assertFalse(mScraper.isAFlightRow(row));

    }

    private Elements getSomeElemets(int noOfElemnts) {

        Elements elements = new Elements();
        for (int i = 0; i < noOfElemnts; i++) {
            elements.add(null);
        }
        return elements;
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