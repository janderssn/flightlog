package no.hauglum.flightlog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Pilot extends BaseEntity{

    private String name;

    private String flightlogId;

    protected Pilot() {
    }

    public Pilot(String flightlogId, String name) {
        this.flightlogId = flightlogId;
        this.name = name;
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

    @Override
    public String toString() {
        return "Pilot{" +
                "mName='" + name + '\'' +
                ", id=" + getId() +
                ", flightlogId='" + flightlogId + '\'' +
                '}';
    }
}
