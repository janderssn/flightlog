package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.FlightGroup;
import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.FlightGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
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
            oneByFlightlogId.setTakeOff(flightGroup.getTakeOff());
            oneByFlightlogId.setHasTrackLog(flightGroup.isHasTrackLog());
            oneByFlightlogId.setDistanceInKm(flightGroup.getDistanceInKm());
            oneByFlightlogId.setDurationInMinutes(flightGroup.getDurationInMinutes());
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

    @Transactional
    public List<FlightGroup> getFlightGroups(String takeOffId, Integer year) {
        List<FlightGroup> flightGroups = mFlightGroupRepository.findByTakeOffIdAndYear(takeOffId, year);
        return flightGroups;
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

    public File getTrackLog(String flightLogId) {
        String trackLogUrl = "https://flightlog.org/fl.html?rqtid=19&trip_id=" + flightLogId;
        File file = new RestTemplate().execute(trackLogUrl, HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
        return file;
    }
}
