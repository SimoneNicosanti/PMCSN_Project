package it.uniroma2.pmcsn.parks.model.event;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class SystemEvent implements Comparable<SystemEvent> {

    // ** The type of events: */
    private final EventType eventType;
    private double eventTime;
    private String centerName;
    private RiderGroup job;

    public SystemEvent(EventType eventType, String centerName, double eventTime,
            RiderGroup job) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.centerName = centerName;
        this.job = job;
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

    public String getCenterName() {
        return this.centerName;
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
