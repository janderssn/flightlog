package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.PilotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PilotService {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());


    @Autowired
    private PilotRepository mPilotRepository;


    public Iterable<Pilot> getPilots() {
        return  mPilotRepository.findAll();
    }


    @Transactional
    public Pilot updateOrCreate(Pilot pilot) {
        Pilot oneByFlightlogId = mPilotRepository.findOneByFlightlogId(pilot.getFlightlogId());
        if(oneByFlightlogId != null){
            oneByFlightlogId.setUpdatedTime(LocalDateTime.now());
            oneByFlightlogId.setName(pilot.getName());
            oneByFlightlogId.setCountry(pilot.getCountry());
            mPilotRepository.save(oneByFlightlogId);
            mLogger.debug("One Pilot updated");
        } else {
            pilot.setUpdatedTime(LocalDateTime.now());
            oneByFlightlogId = mPilotRepository.save(pilot);
            mLogger.debug("New Pilot added to repo");
        }
        return oneByFlightlogId;
    }

    public Long countAll() {
        return mPilotRepository.count();
    }

    public Pilot save(Pilot pilot) {
        return mPilotRepository.save(pilot);
    }

    public Optional<Pilot> get(Long id){
        return mPilotRepository.findById(id);
    }
}
