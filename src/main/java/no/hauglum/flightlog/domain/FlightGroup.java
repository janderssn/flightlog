package no.hauglum.flightlog.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FlightGroup extends BaseEntity{

    private String flightlogId;
    private Type type;

    @ManyToOne
    private Pilot pilot;

    private int noOfFlights;

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

    public enum Type  {
        PG(""),
        HG(""),
        HG2(""),
        PPG(""),
        PHG(""),
        SAILPLAIN(""),
        BALOON(""),
        SPG(""),
        TANDEM_PG("Tandem paragliding")
        ;

        private final String mDesc;

        Type(String desc) {
            mDesc = desc;
        }

        public CharSequence getImageName() {
            return mDesc;
        }
    }
}
