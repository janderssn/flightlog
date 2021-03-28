package no.hauglum.flightlog.service;

import de.micromata.opengis.kml.v_2_2_0.*;
import no.hauglum.flightlog.util.NamespaceFilter;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// https://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
@Service
public class KMLService {

    public List<Coordinate> getCoordinates(File tracklog) throws Exception {

        JAXBContext jc = JAXBContext.newInstance(Kml.class);

        //Create an XMLReader to use with our filter
        XMLReader reader = XMLReaderFactory.createXMLReader();

        //Create the filter (to add namespace) and set the xmlReader as its parent.
        NamespaceFilter inFilter = new NamespaceFilter("http://www.opengis.net/kml/2.2", true);
        inFilter.setParent(reader);

        //Prepare the input, in this case a java.io.File (output)
        InputSource is = new InputSource(new FileInputStream(tracklog));

        //Create a SAXSource specifying the filter
        SAXSource source = new SAXSource(inFilter, is);

        //Do unmarshalling
        Unmarshaller u = jc.createUnmarshaller();
        Document doc = (Document) u.unmarshal(source);

        Folder folder = (Folder) doc.getFeature().get(0);
        Optional<Feature> trackLogFeature = folder.getFeature().stream()
                .filter(this::trackLogFilter)
                .findFirst();

        Geometry geometry = ((Placemark) trackLogFeature.get()).getGeometry();
        LineString lineString = (LineString) geometry;

        return lineString.getCoordinates();
    }

    private boolean trackLogFilter(Feature f) {
        return f.getName().equals("Tracklog");
    }
}
