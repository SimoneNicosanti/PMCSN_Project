package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class SystemEvent implements Comparable<SystemEvent> {

    private EventsPoolId id;
    private double eventTime;
    private Center<RiderGroup> eventCenter;
    private RiderGroup job;

    public SystemEvent(EventsPoolId id, Center<RiderGroup> eventCenter, double eventTime, RiderGroup job) {
        this.id = id;
        this.eventTime = eventTime;
        this.eventCenter = eventCenter;
        this.job = job;
    }

    public void setCenter(Center<RiderGroup> center) {
        this.eventCenter = center;
    }

    public EventsPoolId getPoolId() {
        return this.id;
    }

    public RiderGroup getJob() {
        return job;
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public Center<RiderGroup> getEventCenter() {
        return this.eventCenter;
    }

    public EventType getEventType() {
        return this.id.getEventType();
    }

    public void addServiceTime(double serviceTime) {
        this.eventTime += serviceTime;
    }

    public String getName() {
        return this.id.toString();
    }

    @Override
    public int compareTo(SystemEvent otherEvent) {
        int timeComparison = Double.valueOf(this.eventTime).compareTo(Double.valueOf((otherEvent.eventTime)));
        if (timeComparison != 0) {
            return timeComparison;
        }
        return this.job.getGroupId().compareTo(otherEvent.getJob().getGroupId());
    }

}
