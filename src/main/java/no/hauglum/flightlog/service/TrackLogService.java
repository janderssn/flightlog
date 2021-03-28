package no.hauglum.flightlog.service;

import no.hauglum.flightlog.repository.FlightGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackLogService {

    @Autowired
    private FlightGroupRepository mFlightGroupRepository;



}
