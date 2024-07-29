package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.event.Event;

public interface EventProcessor<T> {

    // Returns new events generated from this processed event
    public List<Event<T>> processEvent(Event<T> event);

}
