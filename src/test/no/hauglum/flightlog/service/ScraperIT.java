package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Country;
import no.hauglum.flightlog.repository.CountryRepository;
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

    @Autowired
    private CountryRepository mCountryRepository;

    @Test
    public void scrapeHovenLoen(){
        mScraper.scapeHovenLoen(2018);
    }

    @Test
    public void scrapeNorway(){
        mScraper.scrapeNorway(2017);
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
    public void scrapeNorwayStartYearEndYear(){
        for (int y = 2017; y < 2019; y++){
            mScraper.scrapeNorway(y, y);
        }
    }

    @Test
    public void scrapeSomeDaysInNorway(){
        String countryId = "160";
        int year = 2018;
        int dayOfYear = 10;
        int offset = 5;

        scrapeSomeDays(countryId, year, dayOfYear, offset);
    }

    @Test
    public void scrapeSomeDaysAllCountries(){

        int year = 2018;
        int dayOfYear = 1;
        int offset = 365;

        for (Country c : mCountryRepository.findAll()){
            scrapeSomeDays(c.getCountryId(), year, dayOfYear, offset);
        }
    }

    private void scrapeSomeDays(String countryId, int year, int dayOfYear, int offset) {
        LocalDate localDate = LocalDate.ofYearDay(year, dayOfYear);
        mScraper.scrapeCountry(countryId, localDate, localDate.plusDays(offset));
    }
}

