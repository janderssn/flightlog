package no.hauglum.flightlog.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ScraperIT {

    @Autowired
    private Scraper mScraper;

    @Test
    public void scrapeHovenLoen(){
        mScraper.scapeHovenLoen(2018);
    }

    @Test
    public void scrapeNorway(){
        mScraper.scrapeNorway(2015);
    }

    @Test
    @Ignore("to be done")
    public void scrapeAllCountries(){


    }

    @Test
    public void loadCountriesToDb(){
        mScraper.loadCountriesToDb();
    }


    @Test
    public void scrapeNorwayFrom2011(){
        for (int y = 2018; y < 2020; y++) {

            mScraper.scrapeNorway(y, y);
        }
    }

    @Test
    public void scrapeOneDay(){


            LocalDate localDate = LocalDate.ofYearDay(2018, 10);
            mScraper.scrapeCountry("160", localDate, localDate.plusDays(20));

    }
}

