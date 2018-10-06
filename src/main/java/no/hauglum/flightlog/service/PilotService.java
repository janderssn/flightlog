package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.repository.PilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.List;

@Service
public class PilotService {

    @Autowired
    private PilotRepository mPilotRepository;

    @Scheduled(cron = "* * * * * *")//= every  seconds.
    public void getNewFlights(){
        System.out.println(" hei");
    }

    public List<Pilot> getPilots() {
        return (List<Pilot>) mPilotRepository.findAll();
    }
}
