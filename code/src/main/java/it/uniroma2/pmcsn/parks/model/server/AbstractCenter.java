package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public abstract class AbstractCenter implements Center<RiderGroup> {

    protected double avgServiceTime;
    protected final String name;
    protected List<RiderGroup> currentServingJobs;
    protected QueueManager<RiderGroup> queueManager;
    protected final Integer slotNumber;

    private RoutingNode<RiderGroup> nextRoutingNode;

    public AbstractCenter(String name, QueueManager<RiderGroup> queueManager, Integer slotNumber,
            Double avgServiceTime) {
        this.name = name;
        this.currentServingJobs = new ArrayList<>();
        this.queueManager = queueManager;
        this.slotNumber = slotNumber;
        this.avgServiceTime = avgServiceTime;
    }

    public double getAvgDuration() {
        return this.avgServiceTime;
    }

    public int getSlotNumber() {
        return this.slotNumber;
    }

    /**
     * Arrival of a new job in the center. If the center is able to serve the job,
     * it adds it to a queue and starts the service, otherwise it just adds
     * the job to the queue.
     */
    protected void commonArrivalManagement(RiderGroup job) {
        int jobSize = job.getGroupSize();

        // Check before adding the job in the queue, otherwise the queue is never empty
        boolean mustServe = this.isQueueEmptyAndCanServe(jobSize);
        this.queueManager.addToQueues(job);

        // If job arrives and can be served immediately, we schedule the new job
        if (mustServe) {
            this.startService();
        }
    }

    /**
     * End service for serving job and schedule the next arrival event based on the
     * next center that is returned by the network routing node.
     */
    protected void scheduleNextEvent(RiderGroup endedJob) {

        // Scheduling arrival to new center
        Center<RiderGroup> center = nextRoutingNode.route(endedJob);
        SystemEvent<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.ARRIVAL, endedJob,
                ClockHandler.getInstance().getClock());
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(newEvent);

        // EventLogger.logEvent("Schedule ", newEvent);
    }

    /**
     * Check if the queue is empty and the center is able to serve a job with size
     * "jobSize"
     */
    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return this.queueManager.areQueuesEmpty() && this.canServe(jobSize);
    }

    /**
     * Check if the center is able to serve a job with size "jobSize"
     */
    public abstract boolean canServe(Integer jobSize);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setNextRoutingNode(RoutingNode<RiderGroup> nextRoutingNode) {
        this.nextRoutingNode = nextRoutingNode;
    }

    /**
     * Start the service and schedule the correlated END_PROCESS events
     */
    public abstract List<RiderGroup> startService();

    /**
     * Return the jobs to serve. If no jobs are available, an empty list is
     * returned.
     */
    protected abstract List<RiderGroup> getJobsToServe();

    /**
     * Return the new service time for the job
     */
    protected abstract Double getNewServiceTime(RiderGroup job);

}