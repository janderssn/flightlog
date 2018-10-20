package no.hauglum.flightlog.repository;

import no.hauglum.flightlog.domain.FlightGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FlightGroupRepository extends CrudRepository<FlightGroup, Long> {


    FlightGroup findOneByFlightlogId(String flightlogId);
}