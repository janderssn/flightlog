package no.hauglum.flightlog.repository;

import no.hauglum.flightlog.domain.Pilot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PilotRepositoryIT {

    @Autowired
    PilotRepository mPilotRepository;

    @Test
    public void findByName() {
        assertTrue(mPilotRepository.findAll() != null);

        String name = "HUBBA";
        Pilot pilot = new Pilot("123", name);
        mPilotRepository.save(pilot);
        List<Pilot> byName = mPilotRepository.findByName(name);

        assertTrue(byName.size() == 1);
        assertTrue(byName.get(0).getFlightlogId().endsWith("123"));

    }

    @Test
    public void findByFlightlogId() {
        assertTrue(mPilotRepository.findAll() != null);

        String name = "HUBBA";
        Pilot pilot = new Pilot("123", name);
        mPilotRepository.save(pilot);
        Pilot oneByFlightlogId = mPilotRepository.findOneByFlightlogId(pilot.getFlightlogId());

        assertTrue(oneByFlightlogId != null);
        assertTrue(oneByFlightlogId.getFlightlogId().endsWith("123"));

    }
}