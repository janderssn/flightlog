package no.hauglum.flightlog.domain.scraper;

import no.hauglum.flightlog.domain.BaseEntity;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * For saving data about scrape jobs
 */
@Entity
public class ScrapeJob extends BaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getJobStartTime() {
        return jobStartTime;
    }

    public LocalDateTime getJobEndTime() {
        return jobEndTime;
    }
}
