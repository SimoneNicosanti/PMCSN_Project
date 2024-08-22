package it.uniroma2.pmcsn.parks.model.event;

import java.util.Objects;

public class EventsPoolId implements Comparable<EventsPoolId> {
    // ** The name of the center to which the pool of events belongs */
    private final String centerName;
    // ** The type of events: */
    private final EventType eventType;

    public EventsPoolId(String centerName, EventType eventType) {
        this.centerName = centerName;
        this.eventType = eventType;
    }

    public String getCenterName() {
        return centerName;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object o) {
        EventsPoolId poolId = (EventsPoolId) o;
        return centerName.equals(centerName) && eventType == poolId.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(centerName, eventType);
    }

    @Override
    public String toString() {
        return centerName + "_" + eventType;
    }

    @Override
    public int compareTo(EventsPoolId arg0) {
        int typeCompare = eventType.compareTo(arg0.eventType);
        if (typeCompare != 0) {
            return typeCompare;
        }
        return centerName.compareTo(arg0.centerName);

    }

}
