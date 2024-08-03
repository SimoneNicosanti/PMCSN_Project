package it.uniroma2.pmcsn.parks.model.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class QueueStatsManager {

    private Map<QueuePriority, Map<RiderGroup, Double>> arrivalTimeMaps;
    private Map<QueuePriority, QueueStats> queueStatsMap;
    protected QueueStats aggregatedStats;
    protected BatchStats queueBatchStats;

    public QueueStatsManager() {
        this.arrivalTimeMaps = new HashMap<>();
        this.queueStatsMap = new HashMap<>();
        this.aggregatedStats = new QueueStats(null);
        this.queueBatchStats = new BatchStats();

        resetQueueStats();
    }

    public List<QueueStats> getQueueStats() {
        return List.copyOf(queueStatsMap.values());
    }

    public QueueStats getAggregatedQueueStats() {
        return this.aggregatedStats;
    }

    public BatchStats getQueueBatchStats() {
        return queueBatchStats;
    }

    public void put(RiderGroup job, QueuePriority priority) {
        if (!arrivalTimeMaps.containsKey(priority)) {
            arrivalTimeMaps.put(priority, new HashMap<>());
            queueStatsMap.put(priority, new QueueStats(priority));
        }

        arrivalTimeMaps.get(priority).put(job, ClockHandler.getInstance().getClock());
    }

    public void remove(RiderGroup job) {

        for (QueuePriority priority : arrivalTimeMaps.keySet()) {
            if (arrivalTimeMaps.get(priority).containsKey(job)) {
                // Remove the group from the map and get the entrance time
                Double entranceTime = arrivalTimeMaps.get(priority).remove(job);

                // Integrity check: every enqued item should have been added in a map
                if (entranceTime == null)
                    throw new RuntimeException("The entrance time must be defined for the enqueued item");

                Double waitingTime = ClockHandler.getInstance().getClock() - entranceTime;

                queueStatsMap.get(priority).updateStats(waitingTime, job.getGroupSize());

                collectAggregatedOnExtract(job, waitingTime);
            }
        }
    }

    protected void collectAggregatedOnExtract(RiderGroup extractedJob, double waitingTime) {
        aggregatedStats.updateStats(waitingTime, extractedJob.getGroupSize());
        queueBatchStats.addTime(waitingTime);
    }

    public Double getArrivalTime(RiderGroup job) {
        for (QueuePriority priority : arrivalTimeMaps.keySet()) {
            if (arrivalTimeMaps.get(priority).containsKey(job)) {
                return arrivalTimeMaps.get(priority).get(job);
            }
        }

        throw new RuntimeException("The job is not in any queue");
    }

    public void resetQueueStats() {
        for (QueuePriority priority : QueuePriority.values()) {
            queueStatsMap.put(priority, new QueueStats(priority));
        }

        aggregatedStats = new QueueStats(null);
    }

}