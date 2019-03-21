package no.hauglum.flightlog.domain;

import javax.persistence.Entity;

@Entity
public class Country extends BaseEntity{
    private String name;
    private String countryId;

    public Country() {
    }

    public Country(String countryId, String countryName) {
        this.name = countryName;
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
