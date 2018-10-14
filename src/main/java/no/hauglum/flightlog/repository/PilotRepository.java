package no.hauglum.flightlog.repository;

import no.hauglum.flightlog.domain.Pilot;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PilotRepository extends CrudRepository<Pilot, Long> {

    List<Pilot> findByName(String name);

    Pilot findOneByFlightlogId(String flightlogId);
}