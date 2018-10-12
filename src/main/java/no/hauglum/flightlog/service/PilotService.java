package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.PilotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.List;

@Service
public class PilotService {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());


    @Autowired
    private PilotRepository mPilotRepository;


    public List<Pilot> getPilots() {
        return (List<Pilot>) mPilotRepository.findAll();
    }

    @Scheduled(cron="${findNewFlightsCron}")
    private void findNewFlights(){
        mLogger.info("Start scraping");
        mLogger.info("Done scraping");
    }
}
