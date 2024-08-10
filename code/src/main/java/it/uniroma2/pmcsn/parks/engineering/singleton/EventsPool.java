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

    /**
     * Free the event pool at the park closure. All events will be deleted except
     * those for termination of the current services and the arrival to the
     * ExitCenter.
     */
    // public void freePool(Center<T> exitCenter) {
    // for (Entry<EventsPoolId, List<SystemEvent<T>>> entry :
    // this.eventMap.entrySet()) {
    // EventsPoolId key = entry.getKey();

    // if (key.getCenterName().equals(Constants.EXIT)
    // || key.getEventType() == EventType.END_PROCESS) {
    // continue;
    // }

    // if (key.getEventType() == EventType.ARRIVAL) {
    // for (SystemEvent<T> event : entry.getValue()) {
    // event.setCenter(exitCenter);

    // }
    // continue;
    // }

    // // Free the list items
    // entry.getValue().clear();
    // }
    // }

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

    public void resetPool() {
        this.eventMap = new HashMap<>();
    }

}
