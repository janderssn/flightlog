package no.hauglum.flightlog.service;

import no.hauglum.flightlog.FatalException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentFactory {

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


    public List<DocumentWrapper> getLogForTakeOff(int startYear, String takaOffId) {
        List<DocumentWrapper> documents = new ArrayList<>();
        for (int year = startYear; year < LocalDate.now().getYear() +1; year++) {

            int offset = 0;
            int pageSize = 1000;
            for (int page = 0; page < 10; page++) {
                offset = page * pageSize;
                String url1 = "https://no.flightlog.org/fl.html?l=2&country_id=160&start_id=" + takaOffId + "&a=42&year=" + year + "&offset=" + offset;
                DocumentWrapper dw = new DocumentWrapper(scrape(url1), year);
                documents.add(dw);
                if(getRowsInTable(dw.getDocument()).size() < 3)
                    break;
            }
        }
        return documents;
    }

    public Elements getRowsInTable(Document doc) {
        Elements tables = doc.select("table");
        int noOfTables = tables.size();
        Element table = tables.get(noOfTables - 1);
        return table.select("tr");
    }
}
