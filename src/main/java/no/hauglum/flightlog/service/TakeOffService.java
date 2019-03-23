package no.hauglum.flightlog.service;

import no.hauglum.flightlog.domain.TakeOff;
import no.hauglum.flightlog.repository.TakeOffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TakeOffService {

    private final TakeOffRepository mTakeOffRepository;

    public TakeOffService(TakeOffRepository mTakeOffRepository) {
        this.mTakeOffRepository = mTakeOffRepository;
    }

    public TakeOff createOrUpdate(TakeOff takeOff) {
        TakeOff byTakeOffId = mTakeOffRepository.findByTakeOffId(takeOff.getTakeOffId());
        if(byTakeOffId != null) {
            updateProperties(takeOff, byTakeOffId);
            byTakeOffId = mTakeOffRepository.save(byTakeOffId);
        }else {
            byTakeOffId = mTakeOffRepository.save(takeOff);
        }

        return byTakeOffId;
    }

    private void updateProperties(TakeOff source, TakeOff target) {
        target.setUpdatedTime(LocalDateTime.now());
        target.setName(source.getName());
        target.setCountry(source.getCountry());
    }


    public long countAll() {
        return mTakeOffRepository.count();
    }

}
