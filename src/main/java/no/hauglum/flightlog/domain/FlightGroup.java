package no.hauglum.flightlog.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NamedQuery(name = "FlightGroup.findByTakeOffIdAndYear",
        query = "select f from FlightGroup f where f.takeOff.takeOffId = ?1 and year(date) = ?2")
public class FlightGroup extends BaseEntity {

    private String flightlogId;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    private Pilot pilot;

    private LocalDate date;

    private int noOfFlights;

    private Double distanceInKm;

    private Integer durationInMinutes;

    private Boolean hasTrackLog;

    @ManyToOne
    private TakeOff takeOff;

    protected FlightGroup() {
    }

    public FlightGroup(String flightlogId) {
        this.flightlogId = flightlogId;
    }

    public void setType(Type type) {
        if(type == null)
            throw new IllegalArgumentException("Null is not a type, check image to type conversion");
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setFlightlogId(String flightlogId) {
        this.flightlogId = flightlogId;
    }

    public String getFlightlogId() {
        return flightlogId;
    }

    public Type getType() {
        return type;
    }

    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public void setNoOfFlights(int noOfFlights) {
        this.noOfFlights = noOfFlights;
    }

    public int getNoOfFlights() {
        return noOfFlights;
    }

    public void setTakeOff(TakeOff takeOff) {
        this.takeOff = takeOff;
    }

    public TakeOff getTakeOff() {
        return takeOff;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public boolean isHasTrackLog() {
        return hasTrackLog;
    }

    public void setHasTrackLog(boolean hasTrackLog) {
        this.hasTrackLog = hasTrackLog;
    }

    public enum Type  {
        PG(""),
        HG(""),
        HG2(""),
        PPG(""),
        PHG(""),
        SAILPLAIN(""),
        BALOON(""),
        SPG(""),
        TANDEM_PG("Tandem paragliding"),
        TRACKLOG("")
        ;

        private final String mDesc;

        Type(String desc) {
            mDesc = desc;
        }

        public String getImageName() {
            return mDesc;
        }
    }
}
