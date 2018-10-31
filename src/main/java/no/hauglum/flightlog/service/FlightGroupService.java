package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.FlightGroup;
import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.FlightGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
public class FlightGroupService {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private FlightGroupRepository mFlightGroupRepository;

    @Autowired
    private PilotService mPilotService;

    @Transactional
    public void updateOrCreate(FlightGroup flightGroup) {
        FlightGroup oneByFlightlogId = mFlightGroupRepository.findOneByFlightlogId(flightGroup.getFlightlogId());
        if(oneByFlightlogId != null){
            oneByFlightlogId.setUpdatedTime(LocalDateTime.now());
            oneByFlightlogId.setNoOfFlights(flightGroup.getNoOfFlights());
            oneByFlightlogId.setType(flightGroup.getType());
            mFlightGroupRepository.save(oneByFlightlogId);
            mLogger.debug("One FlightGroup updated");
        } else {
            flightGroup.setUpdatedTime(LocalDateTime.now());
            mFlightGroupRepository.save(flightGroup);
            mLogger.debug("New FlightGroup added to repo");
        }
    }

    public Iterable<FlightGroup> getFlightGroups() {
        return mFlightGroupRepository.findAll();
    }

    public long countAll() {
        return mFlightGroupRepository.count();
    }

    @Transactional
    public void saveAll(List< FlightGroup> flightGroups) {
        flightGroups.stream().forEach(flightGroup -> {
            Pilot pilot = flightGroup.getPilot();
            Optional<Pilot> value = mPilotService.get(pilot.getId());
            pilot = Optional.ofNullable(value).get().orElse(null);
mFlightGroupRepository.save(flightGroup);
        });
        //mFlightGroupRepository.saveAll(flightGroups);
    }
}
