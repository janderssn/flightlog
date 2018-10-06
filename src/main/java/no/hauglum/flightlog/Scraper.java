package no.hauglum.flightlog;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.UnknownHostException;

public class Scraper {
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

    public boolean isADayRow(Element element) {
        String text = element.text();

        return text.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
}
