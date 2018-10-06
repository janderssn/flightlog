package no.hauglum.flightlog;

import no.hauglum.flightlog.service.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private PilotService mPilotService;

    @RequestMapping("/")
    public String index() {
        return "Greetings!" + " There is " + mPilotService.getPilots().size() +  " pilotes in db";
    }
}
