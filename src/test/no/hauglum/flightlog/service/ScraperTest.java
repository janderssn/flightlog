package no.hauglum.flightlog.service;

import no.hauglum.flightlog.FatalException;
import no.hauglum.flightlog.service.Scraper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 *
 */
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
    public void bigTest(){
        mScraper.scrapeFlightlog();
    }

    @Test
    public void canParseUserId() {
        Element element = new Element("hello");
        element.attr("href", "asdkføaksjndøfjanø user_id=1234 sdjfnøajsnødj");
        assertTrue("User id is not parsed","1234".equals(mScraper.parseFlightlogId(element)));
    }

    @Test
    public void canParseUserId2() {
        Element element = new Element("hello");
        element.attr("href", "asdkføaksjndøfjanø user_id=1234");
        assertTrue("User id is not parsed","1234".equals(mScraper.parseFlightlogId(element)));
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