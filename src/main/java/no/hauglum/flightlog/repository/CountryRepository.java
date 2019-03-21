package no.hauglum.flightlog.repository;

import no.hauglum.flightlog.domain.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepository extends CrudRepository<Country, Long> {
    Country findByCountryId(String id);
}
