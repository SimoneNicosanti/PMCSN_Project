package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public abstract class Center implements CenterInterface<RiderGroup> {

    protected final String name;
    protected List<RiderGroup> currentServingJobs;
    protected QueueManager<RiderGroup> queueManager;
    protected final Integer slotNumber;

    private RoutingNode<RiderGroup> nextRoutingNode;

    public Center(String name, QueueManager<RiderGroup> queueManager, Integer slotNumber) {
        this.name = name;
        this.currentServingJobs = new ArrayList<>();
        this.queueManager = queueManager;
        this.slotNumber = slotNumber;
    }

    /**
     * Arrival of a new job in the center. If the center is able to serve the job,
     * it adds it to a queue and starts the service, otherwise it just adds
     * the job to the queue.
     */
    protected void commonArrivalManagement(RiderGroup job) {
        int jobSize = job.getGroupSize();

        // If job arrives and can be served immediately, we schedule the new job
        if (this.isQueueEmptyAndCanServe(jobSize)) {
            this.queueManager.addToQueues(job);
            this.startService();
        } else {
            // If job cannot be served immediately, we add it to queue
            this.queueManager.addToQueues(job);
        }
    }

    /**
     * End service for serving job and schedule the next arrival event based on the
     * next center that is returned by the network routing node.
     */
    protected void commonEndManagement(RiderGroup endedJob) {
        // Removing this job from service
        this.terminateService(endedJob);

        // Scheduling arrival to new center
        CenterInterface<RiderGroup> center = nextRoutingNode.route(endedJob);
        Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.ARRIVAL, endedJob,
                ClockHandler.getInstance().getClock());
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(newEvent);

        EventLogger.logEvent("Schedule ", newEvent);
    }

    /**
     * Check if the queue is empty and the center is able to serve a job with size
     * "jobSize"
     */
    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return this.queueManager.areQueuesEmpty() && this.canServe(jobSize);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setNextRoutingNode(RoutingNode<RiderGroup> nextRoutingNode) {
        this.nextRoutingNode = nextRoutingNode;
    }

    public List<Queue<RiderGroup>> getQueues() {
        return queueManager.getQueues();
    }

    /**
     * Start the service and schedule the correlated END_PROCESS events
     */
    public abstract List<RiderGroup> startService();

    /**
     * Terminate the service for the job and start the next service if possible
     */
    protected abstract void terminateService(RiderGroup endedJob);

    /**
     * Return the jobs to serve. If no jobs are available, an empty list is
     * returned.
     */
    protected abstract List<RiderGroup> getJobsToServe();

    /**
     * Return the new service time for the job
     */
    protected abstract Double getNewServiceTime(RiderGroup job);

    /**
     * Check if the center is able to serve a job with size "jobSize"
     */
    protected abstract boolean canServe(Integer jobSize);

}