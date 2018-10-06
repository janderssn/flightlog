package no.hauglum.flightlog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pilot {

    private String name;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;


    private String flightlogId;

    protected Pilot() {
    }

    public Pilot(String flightlogId, String name) {
        this.flightlogId = flightlogId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFlightlogId() {
        return flightlogId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pilot{" +
                "mName='" + name + '\'' +
                ", id=" + id +
                ", flightlogId='" + flightlogId + '\'' +
                '}';
    }
}
