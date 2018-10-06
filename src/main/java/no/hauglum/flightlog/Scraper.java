package no.hauglum.flightlog;

import no.hauglum.flightlog.domain.DayPass;
import no.hauglum.flightlog.domain.FlightDay;
import no.hauglum.flightlog.domain.Pilot;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class Scraper {
    public static final String USER_ID = "user_id";

    public Document scrape(String url){
        try {
            return Jsoup.connect(url).get();
        } catch (UnknownHostException e) {
            throw new FatalException("Kan ikke koble til " + url, e);
        }catch (HttpStatusException e){
            throw new FatalException("Kan ikke koble til "+url+" pga feil kode " + e.getStatusCode(),e);
        } catch (IOException e) {
            throw new FatalException("Noe har gått galt " + url, e);
        }
    }


    public void scrapeFlightlog() {

        int startYear = 2016;
        String takaOffId = "6111";
        out.println("Rapport for startsted med id " + takaOffId);

        for (int year = startYear; year < LocalDate.now().getYear() +1; year++) {
            HashMap<String, Pilot> pilots = new HashMap<String, Pilot>();
            List<FlightDay> days = new ArrayList<>();
            HashMap<String, DayPass> dayPasses = new HashMap<String, DayPass>();

            int offset = 0;
            int pageSize = 1000;
            for (int page = 0; page < 10; page++) {

                offset = page * pageSize;
                Document doc = scrape("https://no.flightlog.org/fl.html?l=2&country_id=160&start_id=" + takaOffId + "&a=42&year=" + year + "&offset=" + offset);
                Elements tables = doc.select("table");
                int noOfTables = tables.size();
                Element table = tables.get(noOfTables - 1);
                Elements rows = table.select("tr");

                if(rows.size()< 3)
                    break;

                FlightDay flightDay = null;
                for (Element row : rows) {
                    if (isADayRow(row)) {
                        //out.print("**** Found new day ***  " + row.text());
                        flightDay = new FlightDay(row.text());
                        days.add(flightDay);
                    } else if (isAFlightRow(row)) {
                        Elements cells = row.select("td");
                        for (int i = 0; i < cells.size(); i++) {
                            Element cell = cells.get(i);
                            switch (i) {
                                case 0: {
                                    break;
                                }
                                case 1: {
                                    break;
                                }
                                case 2: {
                                    Elements links = cell.select("a");
                                    Element firstLink = links.get(0);
                                    String flightlogId = parseFlightlogId(firstLink);
                                    String name = parseName(firstLink);

                                    Pilot pilot = new Pilot(flightlogId, name);

                                    pilots.put(flightlogId, pilot);

                                    DayPass dayPass = new DayPass(pilot, flightDay);
                                    dayPasses.put(flightDay.getDate() + "-" + pilot.getFlightlogId(), dayPass);

                                    break;
                                }
                                case 3: {
                                    break;
                                }
                                case 4: {
                                    break;
                                }
                                case 5: {
                                    break;
                                }
                            }
                        }
                        cells.stream().forEach(c -> {
                            //out.print(c.text());
                        });
                    } else {
                        //out.println("some other row");
                    }
                }
            }


            out.println("Rapport for år " + year);
            out.println("Antall flydager: " + days.size());
            out.println("Antall unike piloter: " + pilots.size());
            out.println("Antall dagspass " + dayPasses.size());
            out.println();
        }
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
