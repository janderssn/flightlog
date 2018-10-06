package no.hauglum.flightlog.domain;

import java.util.Objects;

public class DayPass {

    private final Pilot mPilot;
    private final FlightDay mFlightDay;

    public DayPass(Pilot pilot, FlightDay flightDay) {
        mPilot = pilot;
        mFlightDay = flightDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayPass)) return false;
        DayPass dayPass = (DayPass) o;
        return Objects.equals(mPilot, dayPass.mPilot) &&
                Objects.equals(mFlightDay, dayPass.mFlightDay);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mPilot, mFlightDay);
    }
}
