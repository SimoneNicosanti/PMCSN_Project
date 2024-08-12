package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.event.SystemEvent;

public interface EventProcessor<T> {

    // Returns new events generated from this processed event
    public List<SystemEvent> processEvent(SystemEvent event);

}
