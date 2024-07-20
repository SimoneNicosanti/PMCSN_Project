package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.Event;

public class EventHandler {

    private Map<String, List<Event>> eventMap ;

    public EventHandler() {
        this.eventMap = new HashMap<String, List<Event>>() ;
    }

    public Event getNextEvent() {
        Event nextEvent = null ;
        String nextListKey = null ;
        for (String eventKey : eventMap.keySet()) {
            List<Event> currentEventList = eventMap.get(eventKey) ;
            if (currentEventList.size() > 0) {
                if (nextEvent == null) {
                    nextEvent = currentEventList.get(0) ;
                    nextListKey = eventKey ;
                }
                else {
                    Event currentEvent = currentEventList.get(0) ;
                    if (nextEvent.getEventTime() > currentEvent.getEventTime()) {
                        nextEvent = currentEvent ;
                        nextListKey = eventKey ;
                    }
                }
            }
        }

        if (nextListKey != null) {
            eventMap.get(nextListKey).remove(0) ;
        }

        return nextEvent ;
    }

    public void addNewEvent(String eventClass, Event newEvent) {
        List<Event> eventList = eventMap.get(eventClass) ;
        if (eventList == null) {
            eventList = new ArrayList<>() ;
            eventMap.put(eventClass, eventList) ;
        }
        eventList.add(newEvent) ;
        eventList.sort(null);
    }

}
