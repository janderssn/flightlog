package no.hauglum.flightlog.domain;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class FlightDayTest {

    @Test
    public void canHandleOddDate() {
        FlightDay fd = new FlightDay("2018-00-00");
        assertTrue(fd.getDate().equals(LocalDate.parse("2018-01-01")));
    }

    @Test
    public void canHandleOddDate2() {
        FlightDay fd = new FlightDay("2018-05-00");
        assertTrue(fd.getDate().equals(LocalDate.parse("2018-05-01")));
    }
}