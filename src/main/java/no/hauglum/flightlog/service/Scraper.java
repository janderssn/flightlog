package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.DayPass;
import no.hauglum.flightlog.domain.FlightDay;
import no.hauglum.flightlog.domain.FlightGroup;
import no.hauglum.flightlog.domain.Pilot;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.hauglum.flightlog.domain.FlightGroup.Type.*;
import static no.hauglum.flightlog.domain.TakeOff.HOVEN_LOEN;

@Service
public class Scraper {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private DocumentFactory mDocumentFactory;

    public static final int INDEX_OF_TD_WITH_FLIGHT_INFO = 1;
    public static final int INDEX_OF_TD_WITH_PILOT_INFO = 2;

    public static final String USER_ID = "user_id";
    public static final String TRIP_ID = "trip_id";

    public void scrapeNorway(int startYear) {
        scrapeCountry("160", startYear);
    }

    private void scrapeCountry(String countryId, int startYear) {
        List<DocumentWrapper> documents = mDocumentFactory.getLogForCountry(countryId, startYear);
        readDocuments(documents);
    }

    public void scapeHovenLoen(int startYear) {
        scrapeTakeOff(startYear, HOVEN_LOEN);
    }

    public void scrapeTakeOff(int startYear, String takeOffId) {
        mLogger.info("Rapport for startsted med id " + takeOffId);
        List<DocumentWrapper> documents = mDocumentFactory.getLogForTakeOff(startYear, takeOffId);
        readDocuments(documents);
    }

    private void readDocuments(List<DocumentWrapper> documents) {
        HashMap<String, Pilot> pilots = new HashMap<String, Pilot>();
        List<FlightDay> days = new ArrayList<>();
        HashMap<String, DayPass> dayPasses = new HashMap<String, DayPass>();
        HashMap<String, FlightGroup> flightGroups = new HashMap<>();

        for (DocumentWrapper dw : documents) {
            Elements rows = mDocumentFactory.getRowsInTable(dw.getDocument());

            FlightDay flightDay = null;
            for (Element row : rows) {
                if (isADayRow(row)) {
                    flightDay = new FlightDay(row.text());
                    days.add(flightDay);
                } else if (isAFlightRow(row)) {
                    Elements cells = row.select("td");
                    //Type of flight
                    Element flightCell = cells.get(INDEX_OF_TD_WITH_FLIGHT_INFO);

                    FlightGroup flightGroup = parseFlightGroup(flightCell);
                    flightGroups.put(flightGroup.getFlightlogId(), flightGroup);


                    //Pilot
                    Element cell = cells.get(INDEX_OF_TD_WITH_PILOT_INFO);
                    Elements links = cell.select("a");
                    Element firstLink = links.get(0);
                    String flightlogId = parseFlightlogId(firstLink);
                    String name = parseName(firstLink);
                    Pilot pilot = new Pilot(flightlogId, name);
                    pilots.put(flightlogId, pilot);

                    //Daypass
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
        mLogger.info(("Antall grupper av turer " + flightGroups.size()));
        mLogger.info("Rapport slutt");
    }

    private FlightGroup parseFlightGroup(Element flightCell) {
        String tripId = parseTripId(flightCell);

        //Todo parse how many trips in group "/2"
        Elements elementsByAttribute = flightCell.getElementsByAttribute("src");
        if(elementsByAttribute.size() == 1){
            mLogger.debug("No track");
        }else if(elementsByAttribute.size() == 2){
            mLogger.debug("With track");
        } else if(elementsByAttribute.size() == 3){
            mLogger.debug("With track and image");
        } else {
            throw new IllegalArgumentException("More images than system handles");
        }

        HashMap<String, FlightGroup.Type> imageNameToType = new HashMap<>();
        imageNameToType.put("img/kkpg-pg.bmp", PG);
        imageNameToType.put("img/hg.gif", HG);
        imageNameToType.put("img/hg2.gif", HG2);
        imageNameToType.put("img/kkpg-ppg.bmp", PPG);
        imageNameToType.put("img/hg-p.gif", PHG);
        imageNameToType.put("img/sp.gif", SAILPLAIN);
        imageNameToType.put("img/kkpg-ba.bmp", BALOON);
        imageNameToType.put("img/kkpg-spg.bmp", SPG);
        imageNameToType.put("img/kkpg-tp.bmp", TANDEM_PG);


        String srcImg = elementsByAttribute.last().attr("src");
        FlightGroup flightGroup = new FlightGroup(tripId);
        if(srcImg.contains("track")){
            throw new IllegalStateException("Wrong image selected for trip type intepretion");
        }
        if (imageNameToType.get(srcImg) == null)
            mLogger.error("type based on image " + srcImg + " not found");
        flightGroup.setType(imageNameToType.get(srcImg));
        return flightGroup;
    }

    protected boolean isADayRow(Element element) {
        String text = element.text();

        return text.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    protected boolean isAFlightRow(Element row) {
        return row.select("td").size() == 6;
    }

    protected String parseFlightlogId(Element element) {
        String userId = USER_ID;
        return getValue(element, userId);
    }

    protected String parseName(Element element) {
        return element.text();
    }

    public String parseTripId(Element element) {
        return getValue(element, TRIP_ID);
    }

    private String getValue(Element element, String id) {
        String href = element.getElementsByAttribute("href").attr("href");
        int indexOf = href.indexOf(id);
        String substring = href.substring(indexOf + id.length() + 1);
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(substring);
        if (m.find()) {
            int position = m.start();
            return substring.substring(0,position);
        }
        return substring;
    }
}
