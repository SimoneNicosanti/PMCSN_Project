package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;

public class EventsPool {

    private static EventsPool instance = null;

    private Map<EventsPoolId, List<SystemEvent>> eventMap;

    public static EventsPool getInstance() {
        if (instance == null) {
            instance = new EventsPool();
        }
        return instance;
    }

    public EventsPool() {
        this.eventMap = new HashMap<>();
    }

    public SystemEvent getNextEvent() {
        SystemEvent nextEvent = null;
        EventsPoolId nextEventsPoolId = null;
        for (EventsPoolId id : eventMap.keySet()) {
            List<SystemEvent> currentEventList = eventMap.get(id);
            if (currentEventList.size() > 0) {
                if (nextEvent == null) {
                    nextEvent = currentEventList.get(0);
                    nextEventsPoolId = id;
                } else {
                    // Lists are ordered, so the first elem is the lowest
                    SystemEvent currentEvent = currentEventList.get(0);
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

    public void scheduleNewEvent(SystemEvent event) {
        EventsPoolId poolId = event.getPoolId();
        List<SystemEvent> eventList = eventMap.get(poolId);
        if (eventList == null) {
            eventList = new ArrayList<>();
            eventMap.put(poolId, eventList);
        }
        eventList.add(event);
        eventList.sort(null);
    }

    public void scheduleNewEvents(List<SystemEvent> events) {
        for (SystemEvent event : events) {
            scheduleNewEvent(event);
        }
    }

    public static void reset() {
        instance = null;
    }

}
