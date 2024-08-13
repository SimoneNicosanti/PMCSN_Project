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

/**
 * Decorator for collecting stats.
 */
public class StatsCenter implements Center<RiderGroup> {

    private Center<RiderGroup> center;

    protected CenterStatistics wholeDayStats;

    protected BatchStats serviceBatchStats;
    protected BatchStats queueBatchStats;

    protected Map<Long, Double> startServingTimeMap;
    private Map<Long, Double> enqueuedTimeMap; // Given the job id, return the job entrance in queue
    protected Map<Long, QueuePriority> priorityMap; // Given the job id, return the job priority in queue

    protected IntervalStatsManager intervalStatsManager;

    private boolean serviceBatchFinished = false;
    private boolean queueBatchFinished = false;

    public StatsCenter(Center<RiderGroup> center) {
        this.center = center;

        this.wholeDayStats = new CenterStatistics();

        this.startServingTimeMap = new HashMap<>();
        this.enqueuedTimeMap = new HashMap<>();
        this.priorityMap = new HashMap<>();

        this.serviceBatchStats = new BatchStats("ServiceTime");
        this.queueBatchStats = new BatchStats("QueueTime");

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
            this.priorityMap.put(job.getGroupId(), priority);
            this.enqueuedTimeMap.put(job.getGroupId(), ClockHandler.getInstance().getClock());
        }

        return priority;
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
        QueuePriority queuePrio = this.priorityMap.remove(job.getGroupId());
        Double enqueueTime = this.enqueuedTimeMap.remove(job.getGroupId());
        Double dequeueTime = currentClock;

        if (center instanceof Attraction) {
            job.getGroupStats().incrementQueueTime(dequeueTime - enqueueTime);
        }

        this.intervalStatsManager.updateQueueTime(enqueueTime, dequeueTime, queuePrio, job.getGroupSize());
        this.wholeDayStats.updateQueueArea(dequeueTime - enqueueTime, queuePrio, job.getGroupSize());
        this.queueBatchStats.addTime(dequeueTime - enqueueTime);
        if (queueBatchStats.isBatchCompleted() && !queueBatchFinished) {
            System.out.println(center.getName() + " QUEUE BATCH COMPLETED");
            queueBatchFinished = true;
        }
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
                // this.wholeDayStats.addServiceTime(jobServiceTime);
            }
            multiplier = endedJob.getGroupSize();
        } else {
            // General management
            // this.wholeDayStats.addServiceTime(jobServiceTime);
        }

        if (center instanceof Entrance || center instanceof Restaurant) {
            multiplier = 1;
        }

        this.intervalStatsManager.updateServiceTime(startServingTime, endServingTime, endedJob.getGroupSize(),
                multiplier);
        this.wholeDayStats.updateServiceArea(endServingTime - startServingTime, endedJob.getGroupSize(), multiplier);

        // if (Constants.MODE == SimulationMode.CONSISTENCY_CHECK && center instanceof
        // Attraction) {
        // // If validation mode and center is an attraction, increment service times
        // // once
        // // for each service -> otherwise the batch will be filled by copy of the same
        // // values

        // if (this.startServingTimeMap.isEmpty())
        // this.serviceBatchStats.addTime(jobServiceTime);

        // } else {
        // this.serviceBatchStats.addTime(jobServiceTime);
        // }

        this.serviceBatchStats.addTime(jobServiceTime);

        if (serviceBatchStats.isBatchCompleted() && !serviceBatchFinished) {
            System.out.println(center.getName() + " SERVICE BATCH COMPLETED");
            serviceBatchFinished = true;
        }

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
                && this.queueBatchStats.isBatchCompleted();
    }

    public BatchStats getServiceBatchStats() {
        return this.serviceBatchStats;
    }

    public BatchStats getQueueBatchStats() {
        return this.queueBatchStats;
    }

    public List<BatchStats> getBatchStats() {
        return List.of(this.serviceBatchStats, this.queueBatchStats);
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
        }

        return removedGroups;
    }

    public Map<Interval, CenterStatistics> getStatsPerInterval() {
        return this.intervalStatsManager.getAllIntervalStats();
    }

    public CenterStatistics getWholeDayStats() {
        return this.wholeDayStats;
    }

    @Override
    public int getSlotNumber() {
        return center.getSlotNumber();
    }

}