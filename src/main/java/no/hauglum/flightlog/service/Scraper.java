package no.hauglum.flightlog.service;

import com.google.common.collect.ImmutableMap;
import no.hauglum.flightlog.domain.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.hauglum.flightlog.domain.FlightGroup.Type.*;
import static no.hauglum.flightlog.domain.TakeOff.HOVEN_LOEN;

@Service
public class Scraper {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private DocumentFactory mDocumentFactory;
    @Autowired
    private PilotService mPilotService;
    @Autowired
    private FlightGroupService mFlightGroupService;
    @Autowired
    private CountryService mCountryService;

    public static final int INDEX_OF_TD_WITH_COUNT_INFO = 3;
    public static final int INDEX_OF_TD_WITH_FLIGHT_INFO = 1;
    public static final int INDEX_OF_TD_WITH_PILOT_INFO = 2;

    public static final String USER_ID = "user_id";
    public static final String TRIP_ID = "trip_id";


    public void loadCountriesToDb() {
        for (int countryId = 0; countryId < 250; countryId++) { //TODO 250?
            Document document = mDocumentFactory.scrape("https://www.flightlog.org/fl.html?l=1&a=48&country_id=" + countryId);
            Elements elementsMatchingText = document.getElementsMatchingText("Flights done by pilots from");
            String h4 = document.select("H4").get(0).text();
            String countryName = h4.substring("Flights done by pilots from ".length());
            mLogger.debug(countryName + " " + countryId);

            if(countryName != "")
                mCountryService.createOrUpdate(new Country(String.valueOf(countryId), countryName));

        }
    }

    public void scrapeNorway(int startYear) {
        scrapeCountry("160", startYear);
    }

    public void scrapeNorway(int startYear, int endYear) {
        scrapeCountry("160", startYear, endYear);
    }

    private void scrapeCountry(String countryId, int startYear) {
        scrapeCountry(countryId, startYear, null);
    }

    private void scrapeCountry(String countryId, int startYear, Integer endYear) {
        endYear = Optional.ofNullable(endYear).orElse(startYear);
        List<DocumentWrapper> documents = mDocumentFactory.getLogForCountry(countryId, startYear, endYear);
        readDocuments(documents);
    }

    public void scrapeCountry(String countryId, LocalDate date, LocalDate date1) {
        List<DocumentWrapper> documents = mDocumentFactory.getLogForCountry(date, date1, countryId);
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

    /**
     * {@link DocumentFactory} produce documents based on the html-pages found in the Flightlog online.
     * These docs will be read here and enteties will be extracted.
     * @param documents
     */
    private void readDocuments(List<DocumentWrapper> documents) {
        List<FlightDay> days = new ArrayList<>();
        HashMap<String, DayPass> dayPasses = new HashMap<String, DayPass>();
        List<FlightGroup> flightGroups = new ArrayList<>();

        for (DocumentWrapper dw : documents) {
            Elements rows = mDocumentFactory.getRowsInTable(dw.getDocument());

            FlightDay flightDay = null;
            for (Element row : rows) {

                if (isADayRow(row)) {
                    flightDay = new FlightDay(row.text());
                    days.add(flightDay);
                } else if (isAFlightRow(row)) {
                    Elements cells = row.select("td");

                    Pilot pilot = parsePilot(cells);
                    pilot = mPilotService.updateOrCreate(pilot);

                    DayPass dayPass = new DayPass(pilot, flightDay);
                    dayPasses.put(flightDay.getDate() + "-" + pilot.getFlightlogId(), dayPass);

                    FlightGroup flightGroup = parseFlightGroup(cells);
                    flightGroup.setDate(flightDay.getDate());
                    flightGroup.setPilot(pilot);
                    flightGroup.setNoOfFlights(parseNoOfFlights(cells));
                    flightGroups.add(flightGroup);
                    mFlightGroupService.updateOrCreate(flightGroup);

                } else {
                    mLogger.debug("some other row in table found");
                }
            }
        }

        mLogger.info("Sluttrapport" );
        mLogger.info("Antall flydager: " + days.size());
        mLogger.info("Antall dagspass " + dayPasses.size());
        mLogger.info(("Antall grupper av turer " + flightGroups.size()));
        mLogger.info("Rapport slutt");
    }

    private Pilot parsePilot(Elements cells) {
        Element cell = cells.get(INDEX_OF_TD_WITH_PILOT_INFO);
        Elements links = cell.select("a");
        Element firstLink = links.get(0);
        String pilotId = parseFlightlogId(firstLink);
        String pilotName = parseName(firstLink);
        Country country = parseCountry(links);
        return new Pilot(pilotId, pilotName, country);
    }

    private Country parseCountry(Elements links) {
        Element lastElement = links.get(links.size() - 1);
        return mCountryService.findByName(lastElement.text());
    }

    private int parseNoOfFlights(Elements cells) {
        Element noOfFlightsCell = cells.get(INDEX_OF_TD_WITH_COUNT_INFO);
        Element elementsMatchingText = noOfFlightsCell.getElementsMatchingText("/").last();
        String text = Optional.ofNullable(elementsMatchingText).map(e -> e.text()).orElse("/ 1");
        return Integer.parseInt(text.substring(text.indexOf("/")+2));
    }

    private FlightGroup parseFlightGroup(Elements cells) {

        Element flightCell = cells.get(INDEX_OF_TD_WITH_FLIGHT_INFO);

        String tripId = parseTripId(flightCell);


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

        String srcImg = elementsByAttribute.last().attr("src");
        FlightGroup flightGroup = new FlightGroup(tripId);
        if(srcImg.contains("track")){
            throw new IllegalStateException("Wrong image selected for trip type intepretion");
        }

        if (FLIGHT_IMAGE_NAME_TO_TYPE.get(srcImg) == null)
            mLogger.error("type based on image " + srcImg + " not found");
        flightGroup.setType(FLIGHT_IMAGE_NAME_TO_TYPE.get(srcImg));
        return flightGroup;
    }

    static final Map<String, FlightGroup.Type> FLIGHT_IMAGE_NAME_TO_TYPE = ImmutableMap.<String, FlightGroup.Type>builder()
    .put("img/kkpg-pg.bmp", PG)
    .put("img/hg.gif", HG)
    .put("img/hg2.gif", HG2)
    .put("img/kkpg-ppg.bmp", PPG)
    .put("img/hg-p.gif", PHG)
    .put("img/sp.gif", SAILPLAIN)
    .put("img/kkpg-ba.bmp", BALOON)
    .put("img/kkpg-spg.bmp", SPG)
    .put("img/kkpg-tp.bmp", TANDEM_PG)
            .build();

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
