package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventHandler<T> {
    
    private Map<Pair<String, EventType>, List<Event<RiderGroup>>> eventMap ;
    private int arrivalStream; //TODO set arrival stream

    public EventHandler() {
        this.eventMap = new HashMap<Pair<String, EventType>, List<Event<RiderGroup>>>() ;
    }

    public Event<RiderGroup> getNextEvent() {
        Event<RiderGroup> nextEvent = null ;
        Pair<String, EventType> nextListKey = null ;
        for (Pair<String, EventType> pair : eventMap.keySet()) {
            List<Event<RiderGroup>> currentEventList = eventMap.get(pair) ;
            if (currentEventList.size() > 0) {
                if (nextEvent == null) {
                    nextEvent = currentEventList.get(0) ;
                    nextListKey = pair ;
                }
                else {
                    // Lists are ordered, so the first elem is the lowest
                    Event<RiderGroup> currentEvent = currentEventList.get(0) ;
                    if (nextEvent.getEventTime() > currentEvent.getEventTime()) {
                        nextEvent = currentEvent ;
                        nextListKey = pair ;
                    }
                }
            }
        }

        if (nextListKey != null) {
            eventMap.get(nextListKey).remove(0) ;
        }

        return nextEvent ;
    }

    public void addNewEvent(Pair<String, EventType> eventKey, Event<RiderGroup> newEvent) {
        List<Event<RiderGroup>> eventList = eventMap.get(eventKey) ;
        if (eventList == null) {
            eventList = new ArrayList<>() ;
            eventMap.put(eventKey, eventList) ;
        }
        eventList.add(newEvent) ;
        eventList.sort(null);
    }

    /**
     * Create the next arrival event.
     */
    public void scheduleNewArrival(Center<RiderGroup> entranceCenter) {
        double currentTime = ClockHandler.getInstance().getClock();
        //TODO find a correct distribution for coming jobs
        double arrivalTime = RandomHandler.getInstance().getExponential(arrivalStream, 1); 

        Pair<String, EventType> eventKey = Pair.of(Config.ENTRANCE, EventType.ARRIVAL);

        RiderGroup job = getNewJob(currentTime);

        Event<RiderGroup> event = new Event<RiderGroup>(entranceCenter, EventType.ARRIVAL, currentTime + arrivalTime, job);

        addNewEvent(eventKey, event);
    }

    private RiderGroup getNewJob(double currentTime) {
        int groupSize = 0; //TODO find a distribution for group sizes
        GroupPriority priority = GroupPriority.NORMAL; //TODO find a distribution for group priorities

        return new RiderGroup(groupSize, priority, currentTime);
    }

}
