package no.hauglum.flightlog.service;

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
        mScraper.scrapeNorway(2017);
    }

    @Test
    public void scrapeNorwayStartYearEndYear(){
        for (int y = 2017; y < 2019; y++) {

            mScraper.scrapeNorway(y, y);
        }
    }

    @Test
    public void scrapeOneDay(){


            LocalDate localDate = LocalDate.ofYearDay(2018, 10);
            mScraper.scrapeCountry("160", localDate, localDate.plusDays(20));

    }
}

