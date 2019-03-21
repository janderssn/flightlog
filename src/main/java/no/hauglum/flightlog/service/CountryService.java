package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.Country;
import no.hauglum.flightlog.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public void createOrUpdate(Country country) {
        Country byCountryId = countryRepository.findByCountryId(country.getCountryId());
        if(byCountryId != null) {
            updateProperties(country, byCountryId);
            countryRepository.save(byCountryId);
        }else {
            countryRepository.save(country);
        }

    }

    private void updateProperties(Country source, Country target) {
        target.setUpdatedTime(LocalDateTime.now());
        target.setName(source.getName());
    }

    public long countAll() {
        return countryRepository.count();
    }
}
