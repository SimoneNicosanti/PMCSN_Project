package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class AttractionQueueManager extends StatsQueueManager {

    private Queue<RiderGroup> priorityQueue;
    private Queue<RiderGroup> normalQueue;

    public AttractionQueueManager() {
        this.priorityQueue = new FifoQueue();
        this.normalQueue = new FifoQueue();

        this.queueStatsMap.put(QueuePriority.NORMAL, new QueueStats(QueuePriority.NORMAL));
        this.queueStatsMap.put(QueuePriority.PRIORITY, new QueueStats(QueuePriority.PRIORITY));
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer numberOfSeats) {
        List<RiderGroup> extractedList = new ArrayList<>();
        int freeSlots = numberOfSeats;
        while (true) {
            if (priorityQueue.getNextSize() <= freeSlots && priorityQueue.getNextSize() != 0) {
                // Get job from priority queue
                RiderGroup riderGroup = doDequeue(priorityQueue, QueuePriority.PRIORITY);
                if (riderGroup == null) {
                    // No one in the queue to serve --> Go to next queue
                    continue;
                }
                freeSlots -= riderGroup.getGroupSize();
                extractedList.add(riderGroup);
            } else if (normalQueue.getNextSize() <= freeSlots && normalQueue.getNextSize() != 0) {
                // Get job from normal queue
                RiderGroup riderGroup = doDequeue(normalQueue, QueuePriority.NORMAL);
                if (riderGroup == null) {
                    // No one in the queue to serve
                    break;
                }
                freeSlots -= riderGroup.getGroupSize();
                extractedList.add(riderGroup);
            } else {
                // No job is available for the number of free seats
                break;
            }
        }

        this.incrementAttractionQueueTimes(extractedList);
        this.commonStatsCollectionOnExtract(extractedList);
        return extractedList;
    }

    private void incrementAttractionQueueTimes(List<RiderGroup> riderGroups) {
        for (RiderGroup group : riderGroups) {
            Double entranceTime = this.entranceTimeMap.get(group);
            Double exitTime = ClockHandler.getInstance().getClock();
            group.getGroupStats().incrementQueueTime(exitTime - entranceTime);
        }
    }

    @Override
    public boolean areQueuesEmpty() {
        return priorityQueue.getNextSize() == 0 && normalQueue.getNextSize() == 0;
    }

    public int queueLength(GroupPriority priority) {
        int queueLength = 0;
        switch (priority) {
            case PRIORITY:
                queueLength = priorityQueue.queueLength();
                break;
            case NORMAL:
                queueLength = normalQueue.queueLength();
                break;
        }
        return queueLength;
    }

    @Override
    public void addToQueues(RiderGroup item) {
        this.commonStatsCollectionOnAdd(item);

        switch (item.getPriority()) {
            case PRIORITY:
                priorityQueue.enqueue(item);
                break;
            case NORMAL:
                normalQueue.enqueue(item);
                break;
        }
    }

}
