package no.hauglum.flightlog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Pilot {

    private String name;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private LocalDateTime updatedTime;

    private String flightlogId;

    protected Pilot() {
    }

    public Pilot(String flightlogId, String name) {
        this.flightlogId = flightlogId;
        this.name = name;LocalDateTime.now();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlightlogId(String flightlogId) {
        this.flightlogId = flightlogId;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
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
