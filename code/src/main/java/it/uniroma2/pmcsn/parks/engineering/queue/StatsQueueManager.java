package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public abstract class StatsQueueManager implements QueueManager<RiderGroup> {

    protected Map<RiderGroup, Double> entranceTimeMap;
    protected Map<QueuePriority, QueueStats> queueStatsMap;
    protected QueueStats generalStats;

    protected StatsQueueManager() {
        this.entranceTimeMap = new HashMap<>();
        this.queueStatsMap = new HashMap<>();
        this.generalStats = new QueueStats(null);
    }

    protected void commonStatsCollectionOnAdd(RiderGroup item) {
        this.entranceTimeMap.put(item, ClockHandler.getInstance().getClock());
    }

    protected void commonStatsCollectionOnExtract(List<RiderGroup> extractedJobs) {
        for (RiderGroup group : extractedJobs) {
            double entranceTime = this.entranceTimeMap.remove(group);
            double waitingTime = ClockHandler.getInstance().getClock() - entranceTime;

            generalStats.updateStats(waitingTime, group.getGroupSize());
        }
    }

    protected RiderGroup doDequeue(Queue<RiderGroup> queue, QueuePriority prio) {
        RiderGroup group = queue.dequeue();

        if (group != null) {
            this.updateStatsForSingleQueue(prio, group);
        }

        return group;
    }

    protected void updateStatsForSingleQueue(QueuePriority prio, RiderGroup group) {
        // Update stat for single queue
        double entranceTime = this.entranceTimeMap.get(group);
        double waitingTime = ClockHandler.getInstance().getClock() - entranceTime;

        QueueStats queueStats = this.queueStatsMap.get(prio);
        queueStats.updateStats(waitingTime, group.getGroupSize());
    }

    public void resetQueueStats() {
        for (QueuePriority priority : QueuePriority.values()) {
            queueStatsMap.put(priority, new QueueStats(priority));
        }

        generalStats = new QueueStats(null);
    }

    public List<QueueStats> getAllQueueStats() {
        List<QueueStats> returnList = new ArrayList<>();
        for (QueueStats queueStat : queueStatsMap.values()) {
            returnList.add(queueStat);
        }
        return returnList;
    }

    public QueueStats getGeneralQueueStats() {
        return this.generalStats;
    }
}
