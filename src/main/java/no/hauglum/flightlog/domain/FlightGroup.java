package no.hauglum.flightlog.domain;

public class FlightGroup {

    private final String mFlightlogId;
    private Type mType;
    private Pilot mPilot;
    private int mNoOfFlights;

    public FlightGroup(String flightlogId) {
        mFlightlogId = flightlogId;
    }

    public void setType(Type type) {
        if(type == null)
            throw new IllegalArgumentException("Null is not a type, check image to type conversion");
        mType = type;
    }

    public String getFlightlogId() {
        return mFlightlogId;
    }

    public Type getType() {
        return mType;
    }

    public void setPilot(Pilot pilot) {
        mPilot = pilot;
    }

    public Pilot getPilot() {
        return mPilot;
    }

    public void setNoOfFlights(int noOfFlights) {
        mNoOfFlights = noOfFlights;
    }

    public int getNoOfFlights() {
        return mNoOfFlights;
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
