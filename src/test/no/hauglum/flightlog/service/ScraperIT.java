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
    public void scapeHovenLoen(){
        mScraper.scapeHovenLoen(2018);
    }

    @Test
    public void scrapeNorway(){
        mScraper.scrapeNorway(2018);
    }
}

