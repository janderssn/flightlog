package no.hauglum.flightlog.service.scraper;

import no.hauglum.flightlog.repository.scraper.ScrapeJobRepository;
import org.springframework.stereotype.Service;

@Service
public class ScrapeJobService {
    private final ScrapeJobRepository mScrapeJobRepository;

    public ScrapeJobService(ScrapeJobRepository scrapeJobRepository) {
        mScrapeJobRepository = scrapeJobRepository;
    }
}
