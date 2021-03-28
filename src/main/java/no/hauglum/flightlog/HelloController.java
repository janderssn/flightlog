package no.hauglum.flightlog;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import no.hauglum.flightlog.domain.FlightGroup;
import no.hauglum.flightlog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HelloController {

    @Autowired
    private PilotService mPilotService;
    @Autowired
    private FlightGroupService mFlightGroupService;
    @Autowired
    private CountryService mCountryService;
    @Autowired
    private TakeOffService mTakeOffService;
    @Autowired
    private KMLService kmlService;

    @Value("${welcome.message}")
    private String welcomeMessage;

    @RequestMapping("/")
    public String index() {
        return welcomeMessage + " </br>There is in db:" +
                "</br> " + mPilotService.countAll() +  " Pilots"+
                "</br> " + mFlightGroupService.countAll() +  " FlightGroups"+
                "</br> " + mCountryService.countAll() +  " Countries" +
                "</br> " + mTakeOffService.countAll() +  " TakeOffs" +
                "";
    }

    @RequestMapping(value = "flights/{takeOffId}/{year}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<FlightResponse> list(@PathVariable("takeOffId") String takeOffId, @PathVariable("year") String year) {
        List<FlightGroup> flightGroups = mFlightGroupService.getFlightGroups(takeOffId, Integer.parseInt(year));
        return flightGroups.stream().map(this::toFlightResponse).collect(Collectors.toList());
    }

    @RequestMapping(value = "tracklogs/{flightLogId}/json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Coordinate>> tracklogJSON(@PathVariable("flightLogId") String flightLogId) throws Exception {
        File tracklog = mFlightGroupService.getTrackLog(flightLogId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(kmlService.getCoordinates(tracklog));
    }

    @RequestMapping(value = "tracklogs/{flightLogId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> tracklogKML(@PathVariable("flightLogId") String flightLogId) throws IOException {
        File tracklog = mFlightGroupService.getTrackLog(flightLogId);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(tracklog));
        return ResponseEntity.ok()
                //.headers(headers)
                .contentLength(tracklog.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    private FlightResponse toFlightResponse(FlightGroup flightGroup) {
        FlightResponse flightResponse = new FlightResponse();
        flightResponse.setDistanceInKm(flightGroup.getDistanceInKm());
        flightResponse.setDurationInMinutes(flightGroup.getDurationInMinutes());
        flightResponse.setFlightId(flightGroup.getFlightlogId());
        flightResponse.setTakeOffId(flightGroup.getTakeOff().getTakeOffId());
        flightResponse.setHasTrackLog(flightGroup.isHasTrackLog());
        flightResponse.setNumberOfFlights(flightGroup.getNoOfFlights());
        flightResponse.setPilotId(flightGroup.getPilot().getFlightlogId());
        flightResponse.setDate(flightGroup.getDate().toString());
        return flightResponse;
    }
}
