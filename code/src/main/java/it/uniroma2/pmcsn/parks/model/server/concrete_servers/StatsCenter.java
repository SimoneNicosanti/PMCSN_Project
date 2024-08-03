package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.QueueStatsManager;

/**
 * Decorator for collecting stats.
 */
public class StatsCenter implements Center<RiderGroup> {

    private Center<RiderGroup> center;

    protected CenterStatistics stats;
    protected QueueStatsManager queueStatsManager;
    protected BatchStats serviceBatchStats;

    protected long groupsInTheCenter;
    protected long peopleInTheCenter;

    protected Map<Long, Double> startServingTimeMap;

    public StatsCenter(Center<RiderGroup> center) {
        this.center = center;

        this.groupsInTheCenter = 0;
        this.peopleInTheCenter = 0;

        this.queueStatsManager = new QueueStatsManager();
        this.stats = new CenterStatistics();
        this.startServingTimeMap = new HashMap<>();
        this.serviceBatchStats = new BatchStats();
    }

    public Center<RiderGroup> getCenter() {
        return center;
    }

    @Override
    public QueuePriority arrival(RiderGroup job) {

        // Collect center stats
        stats.updateAreas(groupsInTheCenter, peopleInTheCenter);
        groupsInTheCenter++;
        peopleInTheCenter += job.getGroupSize();

        // Call arrival
        QueuePriority priority = this.center.arrival(job);

        // Collect queues stats
        this.queueStatsManager.put(job, priority);

        return priority;
    }

    @Override
    public List<RiderGroup> startService() {
        List<RiderGroup> startingJobs = this.center.startService();

        Double currentClock = ClockHandler.getInstance().getClock();
        for (RiderGroup job : startingJobs) {
            Double startQueueTime = this.queueStatsManager.getArrivalTime(job);
            Double queueTime = currentClock - startQueueTime;

            queueStatsManager.remove(job);
            if (center instanceof Attraction) {
                job.getGroupStats().incrementQueueTime(queueTime);
            }

            this.startServingTimeMap.put(job.getGroupId(), currentClock);
        }
        return startingJobs;
    }

    @Override
    public void endService(RiderGroup endedJob) {

        stats.updateAreas(groupsInTheCenter, peopleInTheCenter);
        groupsInTheCenter--;
        peopleInTheCenter -= endedJob.getGroupSize();

        this.collectEndServiceStats(endedJob);

        this.center.endService(endedJob);
    }

    private void collectEndServiceStats(RiderGroup endedJob) {
        double jobServiceTime = this.retrieveServiceTime(endedJob);

        if (center instanceof Attraction) {
            // Attraction management
            endedJob.getGroupStats().incrementRidesInfo(this.getName(), jobServiceTime);

            if (this.startServingTimeMap.isEmpty()) {
                this.stats.addServiceTime(jobServiceTime);
            }
        } else {
            // General management
            this.stats.addServiceTime(jobServiceTime);
        }

        this.stats.endServiceUpdate(jobServiceTime, endedJob.getGroupSize());

        this.serviceBatchStats.addTime(jobServiceTime);
    }

    @Override
    public String getName() {
        return this.center.getName();
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return this.center.isQueueEmptyAndCanServe(jobSize);
    }

    @Override
    public void setNextRoutingNode(RoutingNode<RiderGroup> nextRoutingNode) {
        this.center.setNextRoutingNode(nextRoutingNode);
    }

    public CenterStatistics getCenterStats() {
        stats.setQueueStats(queueStatsManager.getQueueStats());
        stats.setAggregatedQueueStats(queueStatsManager.getAggregatedQueueStats());
        return stats;
    }

    public void resetCenterStats() {
        this.stats = new CenterStatistics();

        this.queueStatsManager.resetQueueStats();
    }

    protected double retrieveServiceTime(RiderGroup endedJob) {
        Double startServingTime = startServingTimeMap.remove(endedJob.getGroupId());

        return ClockHandler.getInstance().getClock() - startServingTime;
    }

    @Override
    public Integer getQueueLenght(GroupPriority prio) {
        return this.center.getQueueLenght(prio);
    }

    @Override
    public Double getPopularity() {
        return this.center.getPopularity();
    }

    // Check if both service and queue batches are completed
    public boolean areBatchesCompleted() {
        return this.serviceBatchStats.isBatchCompleted()
                && this.queueStatsManager.getQueueBatchStats().isBatchCompleted();
    }

    public BatchStats getServiceBatchStats() {
        return this.serviceBatchStats;
    }

    public BatchStats getQueueBatchStats() {
        return this.queueStatsManager.getQueueBatchStats();
    }

    @Override
    public boolean canServe(Integer slots) {
        return center.canServe(slots);
    }

}