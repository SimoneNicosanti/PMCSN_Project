package it.uniroma2.pmcsn.parks.model;

public abstract class Event implements Comparable<Event> {

    private double eventTime ;

    public Event(double eventTime) {
        this.eventTime = eventTime ;
    }


    public abstract void process() ;

    public abstract void generateNextEvents() ;

    public double getEventTime() {
        return eventTime;
    }


    @Override
    public int compareTo(Event otherEvent) {
        if (this.getEventTime() < otherEvent.getEventTime()) {
            return -1 ;
        } else if (this.getEventTime() == otherEvent.getEventTime()) {
            return 0 ;
        } else {
            return 1 ;
        }
    }

}
