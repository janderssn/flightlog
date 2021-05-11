package no.hauglum.flightlog.controller;

import no.hauglum.flightlog.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/scraper")
public class ScrapingController {

    @Autowired
    private ScraperService scraperService;

    @GetMapping("{countryId}/{takeOffId}/{startYear}")
    public ResponseEntity<String> scrapeTakeOff(@PathVariable("countryId") String countryId, @PathVariable("takeOffId") String takeOffId, @PathVariable("startYear") int startYear) {
        scraperService.scrapeTakeOff(startYear, countryId, takeOffId);
        return ResponseEntity.ok().build();
    }
}
