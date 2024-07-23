package it.uniroma2.pmcsn.parks.model.event;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.server.Center;

public class Event<T> implements Comparable<Event<T>> {

    private EventsPoolId id;
    private double eventTime;
    private Center<T> eventCenter;
    private List<T> jobList;

    public Event(EventsPoolId id, Center<T> eventCenter, double eventTime, List<T> job) {
        this.id = id;
        this.eventTime = eventTime;
        this.eventCenter = eventCenter;
        this.jobList = job;
    }

    public EventsPoolId getPoolId() {
        return this.id;
    }

    public List<T> getJobList() {
        return jobList;
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
