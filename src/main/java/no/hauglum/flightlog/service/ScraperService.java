package no.hauglum.flightlog.service;

import com.google.common.collect.ImmutableMap;
import no.hauglum.flightlog.exception.FatalException;
import no.hauglum.flightlog.domain.*;
import no.hauglum.flightlog.service.scraper.ScrapeJobService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.hauglum.flightlog.domain.FlightGroup.Type.*;
import static no.hauglum.flightlog.domain.TakeOffs.HOVEN_LOEN;

@Service
public class ScraperService {

    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private DocumentFactory mDocumentFactory;
    @Autowired
    private PilotService mPilotService;
    @Autowired
    private FlightGroupService mFlightGroupService;
    @Autowired
    private CountryService mCountryService;
    @Autowired
    private TakeOffService mTakeOffService ;
    @Autowired
    private ScrapeJobService mScrapeJobService;

    public static final int EXPECTED_NO_OF_CUNTRIES = 250;
    public static final int INDEX_OF_TD_WITH_COUNT_INFO = 3;
    public static final int INDEX_OF_TD_WITH_DISTANCE_INFO = 4;
    public static final int INDEX_OF_TD_WITH_FLIGHT_INFO = 1;
    public static final int INDEX_OF_TD_WITH_PILOT_INFO = 2;

    public static final String USER_ID = "user_id";
    public static final String TRIP_ID = "trip_id";
    private TakeOff lastTakeOff;


    @Scheduled(cron="${findNewFlightsCron}")
    @PostConstruct
    protected void findNewFlights(){
        mLogger.info("Start scraping");
        //TODO finn siste scrape job og fortsett der den slapp
        loadCountriesToDb();
        scrapeTakeOff(2021, "203","1486");
        mLogger.info("Done scraping");
    }

    public void loadCountriesToDb() {
        mLogger.info("Starting loading countries");
        long noOfCuntries = mCountryService.countAll();
        if(noOfCuntries < EXPECTED_NO_OF_CUNTRIES){
            for (int countryId = 0; countryId < EXPECTED_NO_OF_CUNTRIES; countryId++) { //TODO 250?
                Document document = mDocumentFactory.scrape("https://www.flightlog.org/fl.html?l=1&a=48&country_id=" + countryId);
                Elements elementsMatchingText = document.getElementsMatchingText("Flights done by pilots from");
                String h4 = document.select("H4").get(0).text();
                String countryName = h4.substring("Flights done by pilots from ".length());
                mLogger.debug(countryName + " " + countryId);

                if(countryName != "")
                    mCountryService.createOrUpdate(new Country(String.valueOf(countryId), countryName));

            }
        }else {
            mLogger.info("All countries are already loaded");
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
        scrapeTakeOff(startYear, "160", HOVEN_LOEN);
    }

    public void scrapeAre() {
        scrapeTakeOff(2001, "203","431");
    }

    public void scrapeTakeOff(int startYear, String countryId, String takeOffId) {
        mLogger.info("Rapport for startsted med id " + takeOffId);
        List<DocumentWrapper> documents = mDocumentFactory.getLogForTakeOff(startYear, countryId, takeOffId);
        readDocuments(documents);
    }

    /**
     * {@link DocumentFactory} produce documents based on the html-pages found in the Flightlog online.
     * These docs will be read here and enteties will be extracted.
     * @param documents
     */
    private void readDocuments(List<DocumentWrapper> documents) {

        for (DocumentWrapper dw : documents) {
            lastTakeOff = null;

            Elements rows = mDocumentFactory.getRowsInTable(dw.getDocument());

            String name = dw.getDocument().select("h3").text();
            name = name.replace("Turer - ", "");

            FlightDay flightDay = null;
            for (Element row : rows) {

                if (isADayRow(row)) {
                    flightDay = new FlightDay(row.text());
                } else if (isATakeOffRow(row)) {
                    //String name = row.getElementsByAttribute("href").text();
                    String start_id = getValue(row, "start_id");

                    String country_id = getValue(row, "country_id");
                    Country country = mCountryService.findByCountryId(country_id);
                    if(country == null)
                        throw new FatalException("Have u forgot to load the countries?");
                    TakeOff takeOff = new TakeOff(start_id, name, country);
                    takeOff = mTakeOffService.createOrUpdate(takeOff);
                    lastTakeOff = takeOff;
                    mLogger.debug("takeOff: " + takeOff.getName());
                } else if (isAFlightRow(row)) {
                    Elements cells = row.select("td");

                    Pilot pilot = parsePilot(cells);
                    pilot = mPilotService.updateOrCreate(pilot);

                    DayPass dayPass = new DayPass(pilot, flightDay);

                    FlightGroup flightGroup = parseFlightGroup(cells);
                    flightGroup.setDate(flightDay.getDate());
                    flightGroup.setPilot(pilot);
                    flightGroup.setNoOfFlights(parseNoOfFlights(cells));
                    flightGroup.setDistanceInKm(parseDistance(cells));
                    flightGroup.setDurationInMinutes(parseDuration(cells));
                    flightGroup.setTakeOff(lastTakeOff);
                    mFlightGroupService.updateOrCreate(flightGroup);


                }else {
                    mLogger.debug("some other row in table found");
                }
            }
        }
    }

    private boolean isATakeOffRow(Element row) {
        return row.getElementsByAttribute("href").attr("href").contains("start_id");
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

    private int parseDuration(Elements cells) {
        Element durationCell = cells.get(INDEX_OF_TD_WITH_COUNT_INFO);
        String durationText = durationCell.text().substring(0,5);
        String[] durationTextArray = durationText.split(":");
        return Integer.parseInt(durationTextArray[0]) * 60 + Integer.parseInt(durationTextArray[1]);
    }

    private Double parseDistance(Elements cells) {
        Element distanceCell = cells.get(INDEX_OF_TD_WITH_DISTANCE_INFO);
        String distanceText = distanceCell.text();
        if (distanceText.isBlank()) {
            return null;
        }
        return Double.parseDouble(distanceText.replaceAll(" km.*([/].*)?", ""));
    }

    private FlightGroup parseFlightGroup(Elements cells) {

        Element flightCell = cells.get(INDEX_OF_TD_WITH_FLIGHT_INFO);

        String tripId = parseTripId(flightCell);


        Elements elementsByAttribute = flightCell.getElementsByAttribute("src");
        if(elementsByAttribute.size() == 1){
            mLogger.debug("No track");
        }else if(elementsByAttribute.size() == 2){
            mLogger.debug("With track or image");
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

        if (FLIGHT_IMAGE_NAME_TO_TYPE.get(srcImg) == null) {
            mLogger.error("type based on image " + srcImg + " not found");
        }
        flightGroup.setType(FLIGHT_IMAGE_NAME_TO_TYPE.get(srcImg));

        flightGroup.setHasTrackLog(false);
        for (Element e : elementsByAttribute) {
            FlightGroup.Type type = FLIGHT_IMAGE_NAME_TO_TYPE.get(e.attr("src"));
            if (TRACKLOG.equals(type)) {
                flightGroup.setHasTrackLog(true);
            }
        }

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
            .put("img/track2.gif", TRACKLOG)
            .put("img/phototrack.gif", TRACKLOG)
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
        return getValue(href, id);
    }

    private String getValue(String source, String key) {
        int indexOf = source.indexOf(key);
        String substring = source.substring(indexOf + key.length() + 1);
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(substring);
        if (m.find()) {
            int position = m.start();
            return substring.substring(0,position);
        }
        return substring;
    }
}
