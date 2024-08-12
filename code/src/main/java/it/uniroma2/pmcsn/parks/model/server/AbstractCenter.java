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
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public abstract class AbstractCenter implements Center<RiderGroup> {

    protected double avgServiceTime;
    protected final String name;
    protected List<RiderGroup> currentServingJobs;
    protected QueueManager<RiderGroup> queueManager;
    protected final Integer slotNumber;
    protected Double popularity;

    protected boolean isCenterClosed;

    private RoutingNode<RiderGroup> nextRoutingNode;

    public AbstractCenter(String name, QueueManager<RiderGroup> queueManager, Integer slotNumber,
            Double avgServiceTime, Double popularity) {
        this.name = name;
        this.currentServingJobs = new ArrayList<>();
        this.queueManager = queueManager;
        this.slotNumber = slotNumber;
        this.avgServiceTime = avgServiceTime;
        this.popularity = popularity;

        this.isCenterClosed = false;
    }

    public double getAvgDuration() {
        return this.avgServiceTime;
    }

    public int getSlotNumber() {
        return this.slotNumber;
    }

    protected QueuePriority commonArrivalManagement(RiderGroup job) {

        if (isCenterClosed) {
            Center<RiderGroup> nextCenter = this.nextRoutingNode.route(job);
            Double currentClock = ClockHandler.getInstance().getClock();
            EventBuilder.buildEventFrom(nextCenter, EventType.ARRIVAL, job, currentClock);
            return null;
        }

        // int jobSize = job.getGroupSize();

        // Check before adding the job in the queue, otherwise the queue is never empty
        // boolean mustServe = this.isQueueEmptyAndCanServe(jobSize);
        QueuePriority jobPriority = this.queueManager.addToQueues(job);

        // If job arrives and can be served immediately, we schedule the new job
        // if (mustServe) {
        // this.startService();
        // }

        return jobPriority;
    }

    protected void commonEndManagement(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);
        this.scheduleArrivalToNewCenter(endedJob);
    }

    /**
     * End service for serving job and schedule the next arrival event based on the
     * next center that is returned by the network routing node.
     */
    protected void scheduleArrivalToNewCenter(RiderGroup endedJob) {

        // Scheduling arrival to new center
        Center<RiderGroup> center = nextRoutingNode.route(endedJob);
        Double arrivalTime = ClockHandler.getInstance().getClock();

        // Adding this makes the queue times decrease (should we add it??)
        //

        SystemEvent newEvent = EventBuilder.buildEventFrom(center, EventType.ARRIVAL, endedJob,
                arrivalTime);
        EventsPool.getInstance().scheduleNewEvent(newEvent);

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
    public List<RiderGroup> startService() {

        List<RiderGroup> jobsToServe = this.getJobsToServe();

        if (isCenterClosed)
            if (!jobsToServe.isEmpty())
                throw new RuntimeException();

        this.currentServingJobs.addAll(jobsToServe);

        for (RiderGroup job : jobsToServe) {
            double serviceTime = this.getNewServiceTime(job);

            // Schedule an END_PROCESS event
            SystemEvent newEvent = EventBuilder.buildEventFrom(this,
                    EventType.END_PROCESS,
                    job,
                    ClockHandler.getInstance().getClock() + serviceTime);
            EventsPool.getInstance().scheduleNewEvent(newEvent);

            // EventLogger.logEvent("Schedule ", newEvent);
        }

        return jobsToServe;
    }

    /**
     * Return the jobs to serve. If no jobs are available, an empty list is
     * returned.
     */
    protected abstract List<RiderGroup> getJobsToServe();

    /**
     * Return the new service time for the job
     */
    protected abstract Double getNewServiceTime(RiderGroup job);

    @Override
    public List<RiderGroup> closeCenter() {
        List<RiderGroup> removedGroups = this.queueManager.dequeueAll();

        for (RiderGroup group : removedGroups) {
            Center<RiderGroup> nextCenter = nextRoutingNode.route(group);
            Double currentClock = ClockHandler.getInstance().getClock();
            EventBuilder.buildEventFrom(nextCenter, EventType.ARRIVAL, group, currentClock);
        }

        this.isCenterClosed = true;
        return removedGroups;
    }

}