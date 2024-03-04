package nl.uva.doubledatasourceaxon;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ProcessingGroup("some-service-pgroup")
public class SomeService {

    @EventHandler
    public void on(Object event) {}


}
