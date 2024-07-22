package it.uniroma2.pmcsn.parks.model.event;

public class EventsPoolId {
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

}
