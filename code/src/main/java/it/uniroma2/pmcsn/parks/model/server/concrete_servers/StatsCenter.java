package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.IntervalStatsManager;
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
    protected Map<Long, QueuePriority> priorityMap; // Given the job id, return the job priority in queue

    protected IntervalStatsManager intervalStatsManager;

    public StatsCenter(Center<RiderGroup> center) {
        this.center = center;

        this.groupsInTheCenter = 0;
        this.peopleInTheCenter = 0;

        this.queueStatsManager = new QueueStatsManager();
        this.stats = new CenterStatistics();
        this.startServingTimeMap = new HashMap<>();
        this.priorityMap = new HashMap<>();

        this.serviceBatchStats = new BatchStats("ServiceTime");

        this.intervalStatsManager = new IntervalStatsManager();
    }

    public Center<RiderGroup> getCenter() {
        return center;
    }

    @Override
    public QueuePriority arrival(RiderGroup job) {

        // Call arrival
        QueuePriority priority = this.center.arrival(job);

        // Save arrival time
        if (priority != null) {
            // Compute areas
            updateAreas(1, job.getGroupSize());

            this.queueStatsManager.put(job, priority);
            priorityMap.put(job.getGroupId(), priority);
        }

        return priority;
    }

    private void updateAreas(int inc, int groupSizeInc) {
        stats.updateAreas(groupsInTheCenter, peopleInTheCenter);
        groupsInTheCenter += inc;
        peopleInTheCenter += groupSizeInc;
    }

    @Override
    public List<RiderGroup> startService() {
        // Call start service
        List<RiderGroup> startingJobs = this.center.startService();

        // Collect stats of the started jobs
        Double currentClock = ClockHandler.getInstance().getClock();
        for (RiderGroup job : startingJobs) {
            collectQueueTimeStats(currentClock, job);

            // Save start serving time
            this.startServingTimeMap.put(job.getGroupId(), currentClock);
        }
        return startingJobs;
    }

    // Collect stats when the job exits from the queue
    private void collectQueueTimeStats(Double currentClock, RiderGroup job) {
        Double enqueueTime = this.queueStatsManager.getArrivalTime(job);
        Double dequeueTime = currentClock;

        // Update queue time
        queueStatsManager.remove(job);
        if (center instanceof Attraction) {
            job.getGroupStats().incrementQueueTime(dequeueTime - enqueueTime);
        }

        QueuePriority queuePrio = this.priorityMap.get(job.getGroupId());
        intervalStatsManager.updateQueueTime(enqueueTime, dequeueTime, queuePrio, job.getGroupSize());
    }

    @Override
    public void endService(RiderGroup endedJob) {

        this.collectEndServiceStats(endedJob);

        this.center.endService(endedJob);
    }

    private void collectEndServiceStats(RiderGroup endedJob) {
        Double startServingTime = startServingTimeMap.remove(endedJob.getGroupId());
        Double endServingTime = ClockHandler.getInstance().getClock();
        Double jobServiceTime = endServingTime - startServingTime;

        Integer multiplier = 1;
        if (center instanceof Attraction) {
            // Attraction management
            endedJob.getGroupStats().incrementRidesInfo(this.getName(), jobServiceTime);

            if (this.startServingTimeMap.isEmpty()) {
                this.stats.addServiceTime(jobServiceTime);
            }
            multiplier = endedJob.getGroupSize();
        } else {
            // General management
            this.stats.addServiceTime(jobServiceTime);
        }

        if (center instanceof Entrance || center instanceof Restaurant) {
            multiplier = 1;
        }

        // Update area stats
        updateAreas(-1, -endedJob.getGroupSize());
        intervalStatsManager.updateServiceTime(startServingTime, endServingTime, endedJob.getGroupSize(), multiplier);

        // Increment statistics about services
        QueuePriority jobPriority = priorityMap.remove(endedJob.getGroupId());
        this.stats.endServiceUpdate(jobServiceTime, endedJob.getGroupSize(), jobPriority);

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

    public List<BatchStats> getBatchStats() {
        return List.of(serviceBatchStats, queueStatsManager.getQueueBatchStats());
    }

    @Override
    public boolean canServe(Integer slots) {
        return center.canServe(slots);
    }

    @Override
    public List<RiderGroup> closeCenter() {

        List<RiderGroup> removedGroups = this.center.closeCenter();

        Double currentClock = ClockHandler.getInstance().getClock();
        for (RiderGroup job : removedGroups) {
            collectQueueTimeStats(currentClock, job);
            updateAreas(-1, -job.getGroupSize());
        }

        return removedGroups;
    }

    public Map<Interval, CenterStatistics> getStatsPerInterval() {
        return this.intervalStatsManager.getAllIntervalStats();
    }

    public CenterStatistics getWholeDayStats() {
        return this.intervalStatsManager.getTotalStats();
    }

}