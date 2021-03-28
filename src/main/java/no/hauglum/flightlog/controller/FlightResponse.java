package no.hauglum.flightlog.controller;

public class FlightResponse {

    private String takeOffId;
    private String flightId;
    private Integer durationInMinutes;
    private Double distanceInKm;
    private Boolean hasTrackLog;
    private String date;
    private int numberOfFlights;
    private String pilotId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTakeOffId() {
        return takeOffId;
    }

    public void setTakeOffId(String takeOffId) {
        this.takeOffId = takeOffId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public Boolean getHasTrackLog() {
        return hasTrackLog;
    }

    public void setHasTrackLog(Boolean hasTrackLog) {
        this.hasTrackLog = hasTrackLog;
    }

    public void setNumberOfFlights(int numberOfFlights) {
        this.numberOfFlights = numberOfFlights;
    }

    public int getNumberOfFlights() {
        return numberOfFlights;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getPilotId() {
        return pilotId;
    }
}
