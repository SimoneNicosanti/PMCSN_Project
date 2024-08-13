package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;

public class EventsPool {

    private static EventsPool instance = null;

    private Map<EventsPoolId, List<SystemEvent>> eventMap;
    private List<EventsPoolId> sortedEventIdList;

    private List<SystemEvent> totalList;

    public static EventsPool getInstance() {
        if (instance == null) {
            instance = new EventsPool();
        }
        return instance;
    }

    public EventsPool() {
        this.eventMap = new TreeMap<>();
        this.totalList = new ArrayList<>();
        this.sortedEventIdList = new ArrayList<>();
    }

    public SystemEvent getNextEvent() {
        SystemEvent nextEvent = null;
        EventsPoolId nextEventsPoolId = null;

        for (EventsPoolId id : sortedEventIdList) {
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

        SystemEvent totalListNextEvent = null;
        if (totalList.size() > 0) {
            totalListNextEvent = totalList.remove(0);
        }

        if ((totalListNextEvent == null || nextEvent == null) && (totalListNextEvent != nextEvent)) {
            throw new RuntimeException("Inconsistant Lists 1");
        }

        if ((totalListNextEvent != null && nextEvent != null) && totalListNextEvent.compareTo(nextEvent) != 0) {
            throw new RuntimeException("Inconsistant Lists 2");
        }

        return nextEvent;
    }

    public void scheduleNewEvent(SystemEvent event) {
        EventType eventType = event.getEventType();
        String centerName = event.getCenterName();

        EventsPoolId id = null;

        for (EventsPoolId mapKey : eventMap.keySet()) {
            if (mapKey.getEventType() == eventType && mapKey.getCenterName().equals(centerName)) {
                id = mapKey;
                break;
            }
        }

        if (id == null) {
            id = new EventsPoolId(centerName, eventType);
            eventMap.put(id, new ArrayList<>());
            sortedEventIdList.add(id);
            sortedEventIdList.sort(null);
        }

        List<SystemEvent> list = eventMap.get(id);
        list.add(event);
        list.sort(null);

        totalList.add(event);
        totalList.sort(null);
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
