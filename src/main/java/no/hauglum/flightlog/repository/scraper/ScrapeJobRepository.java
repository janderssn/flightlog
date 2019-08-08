package no.hauglum.flightlog.repository.scraper;

import no.hauglum.flightlog.domain.scraper.ScrapeJob;
import org.springframework.data.repository.CrudRepository;

public interface ScrapeJobRepository extends CrudRepository<ScrapeJob, Long> {
}
