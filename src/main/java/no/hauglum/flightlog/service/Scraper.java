package no.hauglum.flightlog.service;

import no.hauglum.flightlog.FatalException;
import no.hauglum.flightlog.domain.DayPass;
import no.hauglum.flightlog.domain.FlightDay;
import no.hauglum.flightlog.domain.Pilot;
import no.hauglum.flightlog.domain.TakeOff;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.hauglum.flightlog.domain.TakeOff.HOVEN_LOEN;

@Service
public class Scraper {

    @Autowired
    private DocumentFactory mDocumentFactory;

    public static final int INDEX_OF_TD_WITH_PILOT_INFO = 2;
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());
    public static final String USER_ID = "user_id";

    public void scrapeFlightlog() {
        int startYear = 2016;
        scrapeTakeOff(startYear, HOVEN_LOEN);
    }

    private void scrapeTakeOff(int startYear, String takeOffId) {
        mLogger.info("Rapport for startsted med id " + takeOffId);
        List<DocumentWrapper> documents = mDocumentFactory.getLogForTakeOff(startYear, takeOffId);
        readDocuments(documents);
    }

    private void readDocuments(List<DocumentWrapper> documents) {
        HashMap<String, Pilot> pilots = new HashMap<String, Pilot>();
        List<FlightDay> days = new ArrayList<>();
        HashMap<String, DayPass> dayPasses = new HashMap<String, DayPass>();

        for (DocumentWrapper dw : documents) {
            Elements rows = mDocumentFactory.getRowsInTable(dw.getDocument());

            FlightDay flightDay = null;
            for (Element row : rows) {
                if (isADayRow(row)) {
                    flightDay = new FlightDay(row.text());
                    days.add(flightDay);
                } else if (isAFlightRow(row)) {
                    Elements cells = row.select("td");
                    Element cell = cells.get(INDEX_OF_TD_WITH_PILOT_INFO);
                    Elements links = cell.select("a");
                    Element firstLink = links.get(0);
                    String flightlogId = parseFlightlogId(firstLink);
                    String name = parseName(firstLink);
                    Pilot pilot = new Pilot(flightlogId, name);
                    pilots.put(flightlogId, pilot);
                    DayPass dayPass = new DayPass(pilot, flightDay);
                    dayPasses.put(flightDay.getDate() + "-" + pilot.getFlightlogId(), dayPass);
                    cells.stream().forEach(c -> {
                    });
                } else {
                    mLogger.debug("some other row in table found");
                }
            }
        }
        mLogger.info("Sluttrapport" );
        mLogger.info("Antall flydager: " + days.size());
        mLogger.info("Antall unike piloter: " + pilots.size());
        mLogger.info("Antall dagspass " + dayPasses.size());
        mLogger.info("Rapport slutt");
    }

    protected boolean isADayRow(Element element) {
        String text = element.text();

        return text.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    protected boolean isAFlightRow(Element row) {
        return row.select("td").size() == 6;
    }

    protected String parseFlightlogId(Element element) {
        String href = element.getElementsByAttribute("href").attr("href");
        int indexOf = href.indexOf(USER_ID);
        String substring = href.substring(indexOf + USER_ID.length() + 1);
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(substring);
        if (m.find()) {
            int position = m.start();
            return substring.substring(0,position);
        }
        return substring;
    }

    protected String parseName(Element element) {
        return element.text();
    }
}
