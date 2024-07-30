package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;

public class EventsPool<T> {

    @SuppressWarnings("rawtypes")
    private static EventsPool instance = null;

    private Map<EventsPoolId, List<SystemEvent<T>>> eventMap;

    @SuppressWarnings("unchecked")
    public static <T> EventsPool<T> getInstance() {
        if (instance == null) {
            instance = new EventsPool<>();
        }
        return instance;
    }

    public EventsPool() {
        this.eventMap = new HashMap<>();
    }

    public SystemEvent<T> getNextEvent() {
        SystemEvent<T> nextEvent = null;
        EventsPoolId nextEventsPoolId = null;
        for (EventsPoolId id : eventMap.keySet()) {
            List<SystemEvent<T>> currentEventList = eventMap.get(id);
            if (currentEventList.size() > 0) {
                if (nextEvent == null) {
                    nextEvent = currentEventList.get(0);
                    nextEventsPoolId = id;
                } else {
                    // Lists are ordered, so the first elem is the lowest
                    SystemEvent<T> currentEvent = currentEventList.get(0);
                    if (nextEvent.getEventTime() > currentEvent.getEventTime()) {
                        nextEvent = currentEvent;
                        nextEventsPoolId = id;
                    }
                }
            }
        }

        if (nextEventsPoolId != null) {
            eventMap.get(nextEventsPoolId).remove(0);
        }

        return nextEvent;
    }

    public void scheduleNewEvent(SystemEvent<T> event) {
        EventsPoolId poolId = event.getPoolId();
        List<SystemEvent<T>> eventList = eventMap.get(poolId);
        if (eventList == null) {
            eventList = new ArrayList<>();
            eventMap.put(poolId, eventList);
        }
        eventList.add(event);
        eventList.sort(null);
    }

    public void scheduleNewEvents(List<SystemEvent<T>> events) {
        for (SystemEvent<T> event : events) {
            scheduleNewEvent(event);
        }
    }

}
