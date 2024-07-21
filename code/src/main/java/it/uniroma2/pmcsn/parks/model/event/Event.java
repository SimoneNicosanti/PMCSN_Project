package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;

public class Event<T> implements Comparable<Event<T>> {

    private double eventTime ;
    private Center<T> eventCenter ;
    private EventType eventType ;
    private T job ;

    public T getJob() {
        return job ;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Event(Center<T> eventCenter, EventType eventType, double eventTime, T job) {
        this.eventTime = eventTime ;
        this.eventCenter = eventCenter ;
        this.eventType = eventType ;
        this.job = job ;
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public Center<T> getEventCenter() {
        return this.eventCenter;
    }


    @Override
    public int compareTo(Event<T> otherEvent) {
        if (this.getEventTime() < otherEvent.getEventTime()) {
            return -1 ;
        } else if (this.getEventTime() == otherEvent.getEventTime()) {
            return 0 ;
        } else {
            return 1 ;
        }
    }



}
