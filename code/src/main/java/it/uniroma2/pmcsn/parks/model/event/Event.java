package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;

public class Event<T> implements Comparable<Event<T>> {

    private EventsPoolId id;
    private double eventTime;
    private CenterInterface<T> eventCenter;
    private T job;

    public Event(EventsPoolId id, CenterInterface<T> eventCenter, double eventTime, T job) {
        this.id = id;
        this.eventTime = eventTime;
        this.eventCenter = eventCenter;
        this.job = job;
    }

    public EventsPoolId getPoolId() {
        return this.id;
    }

    public T getJob() {
        return job;
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public CenterInterface<T> getEventCenter() {
        return this.eventCenter;
    }

    public EventType getEventType() {
        return this.id.getEventType();
    }

    @Override
    public int compareTo(Event<T> otherEvent) {
        if (this.getEventTime() < otherEvent.getEventTime()) {
            return -1;
        } else if (this.getEventTime() == otherEvent.getEventTime()) {
            return 0;
        } else {
            return 1;
        }
    }

    public void addServiceTime(double serviceTime) {
        this.eventTime += serviceTime;
    }

    public String getName() {
        return this.id.toString();
    }

}
