package no.hauglum.flightlog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ScraperIT {

    @Autowired
    private Scraper mScraper;

    @Test
    public void canFindAndUpdateFlightsAndPilots(){
        mScraper.scrapeFlightlog("https://www.flightlog.org/fl.html?l=1&a=47&country_id=160&year=2018&tripdate=2018-10-02");
    }
}

