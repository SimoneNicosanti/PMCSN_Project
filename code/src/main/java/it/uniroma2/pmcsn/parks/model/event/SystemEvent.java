package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;

public class SystemEvent<T> implements Comparable<SystemEvent<T>> {

    private EventsPoolId id;
    private double eventTime;
    private Center<T> eventCenter;
    private T job;

    public SystemEvent(EventsPoolId id, Center<T> eventCenter, double eventTime, T job) {
        this.id = id;
        this.eventTime = eventTime;
        this.eventCenter = eventCenter;
        this.job = job;
    }

    public void setCenter(Center<T> center) {
        this.eventCenter = center;
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

    public Center<T> getEventCenter() {
        return this.eventCenter;
    }

    public EventType getEventType() {
        return this.id.getEventType();
    }

    @Override
    public int compareTo(SystemEvent<T> otherEvent) {
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
