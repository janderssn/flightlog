package no.hauglum.flightlog.repository;

import no.hauglum.flightlog.domain.TakeOff;
import org.springframework.data.repository.CrudRepository;

public interface TakeOffRepository extends CrudRepository<TakeOff, Long> {
    TakeOff findByTakeOffId(String id);

    TakeOff findByName(String name);
}
