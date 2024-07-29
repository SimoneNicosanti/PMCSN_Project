package it.uniroma2.pmcsn.parks.model.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.queue.StatsQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public abstract class StatsCenter extends AbstractCenter {

    protected CenterStats stats;
    protected Map<Long, Double> startServingTimeMap;

    public StatsCenter(String name, StatsQueueManager queueManager, Integer slotNumber) {
        super(name, queueManager, slotNumber);

        // TODO Not good doing this
        // Other way may be pass the CenterStats in the constructor
        // But in that way we would lose transparency in Center about statistics
        this.stats = new CenterStats();

        this.startServingTimeMap = new HashMap<>();
    }

    protected double getServiceTime(RiderGroup endedJob) {
        Double startServingTime = startServingTimeMap.remove(endedJob.getGroupId());

        return ClockHandler.getInstance().getClock() - startServingTime;
    }

    public void resetCenterStats() {
        this.stats = new CenterStats();

        StatsQueueManager statsQueueManager = (StatsQueueManager) this.queueManager; // Perdoname Emanuele por mi vida
                                                                                     // loca <3
        statsQueueManager.resetQueueStats();
    }

    public CenterStats getCenterStats() {
        return stats;
    }

    public List<QueueStats> getQueueStats() {
        return queueManager.getAllQueueStats();
    }

    public QueueStats getGeneralQueueStats() {
        return queueManager.getGeneralQueueStats();
    }

    protected void manageArrival(RiderGroup job) {
        this.collectArrivalStats(job);
        this.commonArrivalManagement(job);
    }

    @Override
    public List<RiderGroup> startService() {
        // Start service
        List<RiderGroup> servingGroups = this.doStartService();

        // Collect data
        for (RiderGroup group : servingGroups) {
            startServingTimeMap.put(group.getGroupId(), ClockHandler.getInstance().getClock());
        }

        return servingGroups;
    }

    protected void manageEndService(RiderGroup endedJob) {
        this.commonEndManagement(endedJob);
        this.collectEndServiceStats(endedJob);
    }

    protected abstract void collectEndServiceStats(RiderGroup endedJob);

    // Method useful for collecting new stats
    protected void collectArrivalStats(RiderGroup job) {
    }

    /**
     * Take jobs from the queue, start a new service and schedule the events for the
     * jobs in service
     */
    protected List<RiderGroup> doStartService() {
        List<RiderGroup> jobsToServe = this.getJobsToServe();

        this.currentServingJobs.addAll(jobsToServe);

        for (RiderGroup job : jobsToServe) {
            double serviceTime = this.getNewServiceTime(job);

            // Schedule an END_PROCESS event
            Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(this,
                    EventType.END_PROCESS,
                    job,
                    ClockHandler.getInstance().getClock() + serviceTime);
            EventsPool.<RiderGroup>getInstance().scheduleNewEvent(newEvent);

            EventLogger.logEvent("Schedule ", newEvent);
        }

        return jobsToServe;
    }

}