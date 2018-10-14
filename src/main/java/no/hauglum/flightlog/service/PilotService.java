package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.PilotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    protected void findNewFlights(){
        mLogger.info("Start scraping");
        mLogger.info("Done scraping");
    }

    @Transactional
    public void updateOrCreate(Pilot pilot) {
        Pilot oneByFlightlogId = mPilotRepository.findOneByFlightlogId(pilot.getFlightlogId());
        if(oneByFlightlogId != null){
            oneByFlightlogId.setName(pilot.getName());
            oneByFlightlogId.setUpdatedTime(pilot.getUpdatedTime());
            mPilotRepository.save(oneByFlightlogId);
        } else {
            mPilotRepository.save(pilot);
        }
    }
}
