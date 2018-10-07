package no.hauglum.flightlog;

import no.hauglum.flightlog.service.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private PilotService mPilotService;

    @Value("${welcome.message}")
    private String welcomeMessage;

    @RequestMapping("/")
    public String index() {
        return welcomeMessage + " There is " + mPilotService.getPilots().size() +  " pilotes in db";
    }
}
