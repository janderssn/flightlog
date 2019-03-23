package no.hauglum.flightlog.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Pilot extends BaseEntity{

    private String name;

    private String flightlogId;

    @ManyToOne
    private Country country;

    protected Pilot() {
    }

    public Pilot(String flightlogId, String name) {
        this.flightlogId = flightlogId;
        this.name = name;
    }

    public Pilot(String pilotId, String pilotName, Country country) {
        this(pilotId, pilotName);
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlightlogId(String flightlogId) {
        this.flightlogId = flightlogId;
    }

    public String getName() {
        return name;
    }

    public String getFlightlogId() {
        return flightlogId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Pilot{" +
                "mName='" + name + '\'' +
                ", id=" + getId() +
                ", flightlogId='" + flightlogId + '\'' +
                '}';
    }
}
