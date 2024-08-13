package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class SystemEvent implements Comparable<SystemEvent> {

    // ** The type of events: */
    private final EventType eventType;
    private double eventTime;
    private Center<RiderGroup> eventCenter;
    private RiderGroup job;

    public SystemEvent(EventType eventType, Center<RiderGroup> eventCenter, double eventTime,
            RiderGroup job) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.eventCenter = eventCenter;
        this.job = job;
    }

    public void setCenter(Center<RiderGroup> center) {
        this.eventCenter = center;
    }

    public String getCenterName() {
        return this.eventCenter.getName();
    }

    public EventType getEventType() {
        return this.eventType;
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

    public void addServiceTime(double serviceTime) {
        this.eventTime += serviceTime;
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
