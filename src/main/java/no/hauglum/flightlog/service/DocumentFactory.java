package no.hauglum.flightlog.service;

import no.hauglum.flightlog.FatalException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
public class DocumentFactory {
    private Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());


    public Document scrape(String url){
        try {
            mLogger.debug("Reading html in url: " + url);
            return Jsoup.connect(url).get();
        } catch (UnknownHostException e) {
            throw new FatalException("Kan ikke koble til " + url, e);
        }catch (HttpStatusException e){
            throw new FatalException("Kan ikke koble til "+url+" pga feil kode " + e.getStatusCode(),e);
        } catch (IOException e) {
            throw new FatalException("Noe har gått galt " + url, e);
        }
    }


    public List<DocumentWrapper> getLogForCountry(LocalDate startDate, LocalDate endDate, String countryId) {
        String main = "https://www.flightlog.org/fl.html?l=1&a=47&country_id=" +
                countryId;

        List<DocumentWrapper> documents = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            String formattedDate = ISO_LOCAL_DATE.format(date);
            String urlWithYear = main + "&tripdate=" + formattedDate;
            paging(documents, urlWithYear);
        }
        return documents;
    }

    public List<DocumentWrapper> getLogForTakeOff(int startYear, String takaOffId) {
        String main = "https://no.flightlog.org/fl.html?l=2&country_id=160&start_id=" + takaOffId + "&a=42";

        List<DocumentWrapper> documents = new ArrayList<>();
        for (int year = startYear; year < LocalDate.now().getYear() +1; year++) {
            String urlWithYear = main +"&year=" + year;
            paging(documents, urlWithYear);
        }
        return documents;
    }

    private void paging(List<DocumentWrapper> documents, String urlWithYear) {
        int offset = 0;
        int pageSize = 1000;
        for (int page = 0; page < 10; page++) {
            offset = page * pageSize;
            String urlWithOffset = urlWithYear + "&offset=" + offset;

            DocumentWrapper dw = new DocumentWrapper(scrape(urlWithOffset));
            if(getRowsInTable(dw.getDocument()).size() < 3) {
                break;
            }
            documents.add(dw);
        }
    }

    public Elements getRowsInTable(Document doc) {
        Elements tables = doc.select("table");
        int noOfTables = tables.size();
        Element table = tables.get(noOfTables - 1);
        return table.select("tr");
    }

    public List<DocumentWrapper> getLogForCountry(String countryId, int startYear) {
        return getLogForCountry(LocalDate.of(startYear, 1, 1), LocalDate.now(), countryId);
    }
}
