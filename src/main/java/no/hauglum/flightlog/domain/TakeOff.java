package no.hauglum.flightlog.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class TakeOff extends BaseEntity{

    private String takeOffId;
    private String name;
    @ManyToOne
    private Country country;

    public TakeOff() {
    }

    public TakeOff(String takeOffId, String name, Country country) {
        this.takeOffId = takeOffId;
        this.name = name;
        this.country = country;
    }

    public String getTakeOffId() {
        return takeOffId;
    }

    public void setTakeOffId(String takeOffId) {
        this.takeOffId = takeOffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
