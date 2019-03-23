package no.hauglum.flightlog;

import no.hauglum.flightlog.service.CountryService;
import no.hauglum.flightlog.service.FlightGroupService;
import no.hauglum.flightlog.service.PilotService;
import no.hauglum.flightlog.service.TakeOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
