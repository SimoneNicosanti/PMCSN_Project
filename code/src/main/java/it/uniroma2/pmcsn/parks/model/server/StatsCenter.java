package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.StatsQueue;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.stats.AttractionStats;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public abstract class StatsCenter extends AbstractCenter {

    protected CenterStats stats;
    protected Map<Long, Double> startServingTimeMap;
    protected Map<Long, Double> queueEntranceTimeMap;
    private List<StatsQueue<RiderGroup>> queues;

    public StatsCenter(String name, QueueManager<RiderGroup> queueManager, Integer slotNumber) {
        super(name, queueManager, slotNumber);

        // TODO Not good doing this
        // Other way may be pass the CenterStats in the constructor
        // But in that way we would lose transparency in Center about statistics
        if (this instanceof Attraction) {
            this.stats = new AttractionStats();
        } else {
            this.stats = new CenterStats();
        }
        this.startServingTimeMap = new HashMap<>();
        this.queueEntranceTimeMap = new HashMap<>();
        this.queues = new ArrayList<>();

        List<Queue<RiderGroup>> centerQueues = this.queueManager.getQueues();

        for (Queue<RiderGroup> queue : centerQueues) {
            if (!(queue instanceof StatsQueue<RiderGroup>))
                throw new RuntimeException("Expected a center with all stats queues");

            this.queues.add((StatsQueue<RiderGroup>) queue);
        }

    }

    protected abstract void doArrival(RiderGroup job);

    protected abstract void doEndService(RiderGroup endedJob);

    protected double getServiceTime(RiderGroup endedJob) {
        Double startServingTime = startServingTimeMap.remove(endedJob.getGroupId());

        return ClockHandler.getInstance().getClock() - startServingTime;
    }

    public void resetCenterStats() {
        if (this instanceof Attraction) {
            this.stats = new AttractionStats();
        } else {
            this.stats = new CenterStats();
        }
    }

    public CenterStats getCenterStats() {
        stats.setQueueStats(getQueueStats());

        return stats;
    }

    private List<QueueStats> getQueueStats() {
        List<QueueStats> queueStats = new ArrayList<>();

        for (StatsQueue<RiderGroup> queue : this.queues) {
            queueStats.add(queue.getQueueStats());
        }

        return queueStats;
    }

    @Override
    public void arrival(RiderGroup job) {

        this.queueEntranceTimeMap.put(job.getGroupId(), ClockHandler.getInstance().getClock());

        this.collectArrivalStats(job);
        this.commonArrivalManagement(job);
        this.doArrival(job);
    }

    @Override
    public List<RiderGroup> startService() {

        // Start service
        List<RiderGroup> servingGroups = this.doStartService();

        // Collect data
        for (RiderGroup group : servingGroups) {
            Double queueEntranceTime = this.queueEntranceTimeMap.remove(group.getGroupId());
            Double enquedTime = ClockHandler.getInstance().getClock() - queueEntranceTime;
            this.stats.addQueueTime(enquedTime);

            startServingTimeMap.put(group.getGroupId(), ClockHandler.getInstance().getClock());
        }

        return servingGroups;
    }

    @Override
    public void endService(RiderGroup endedJob) {

        this.collectEndServiceStats(endedJob);
        this.commonEndManagement(endedJob);
        this.doEndService(endedJob);

        return;
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