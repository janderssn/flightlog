package no.hauglum.flightlog.domain;

import java.time.LocalDate;

public class FlightDay {

    private final LocalDate mDate;

    public FlightDay(String date) {
        if(date.endsWith("00-00")){
            date = date.substring(0, date.length()-5)+ "01-01";
        }
        if(date.endsWith("00"))
            date = date.substring(0,date.length()-2) + "01";

        mDate = LocalDate.parse(date);
    }

    public LocalDate getDate() {
        return mDate;
    }
}
