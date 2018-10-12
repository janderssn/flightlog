package no.hauglum.flightlog.service;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class DocumentFactoryIT {

    @Test
    public void getLogForNorway() {
        DocumentFactory documentFactory = new DocumentFactory();
        LocalDate startDate = LocalDate.of(2018, 10, 2);
        List<DocumentWrapper> documentWrappers = documentFactory.getLogForCountry(startDate, startDate.plusDays(1), "160");
        assertEquals("En dag burde gi en side med flyturer", 1, documentWrappers.size());
    }

    @Test
    public void getLogForNorway10Days() {
        DocumentFactory documentFactory = new DocumentFactory();
        LocalDate stabelDate = LocalDate.of(2017, 9, 20);
        List<DocumentWrapper> documentWrappers = documentFactory.getLogForCountry(stabelDate, stabelDate.plusDays(10), "160");
        assertEquals(
                "10 dager burde gi 10 sider med flyturer (forutsetter flyging hver dag og ikke over 1000 turer på en dag)"
                , 10
                , documentWrappers.size());
    }
}