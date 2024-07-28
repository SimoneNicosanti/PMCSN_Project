package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class RestaurantQueueManager extends StatsQueueManager {

    private Queue<RiderGroup> normalQueue;

    public RestaurantQueueManager() {
        this.normalQueue = new FifoQueue();
        this.queueStatsMap.put(QueuePriority.NORMAL, new QueueStats(QueuePriority.NORMAL));

    }

    @Override
    public boolean areQueuesEmpty() {
        return normalQueue.getNextSize() == 0;
    }

    @Override
    public void addToQueues(RiderGroup item) {
        this.commonStatsCollectionOnAdd(item);
        normalQueue.enqueue(item);
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        List<RiderGroup> extractedGroups = new ArrayList<>();

        int freeSlots = slotNumber;
        while (true) {
            int nextGroupSize = normalQueue.getNextSize();
            if (nextGroupSize > 0 && nextGroupSize <= freeSlots) {
                RiderGroup group = doDequeue(this.normalQueue, QueuePriority.NORMAL);
                extractedGroups.add(group);

                freeSlots -= nextGroupSize;

            } else {
                break;
            }
        }

        this.commonStatsCollectionOnExtract(extractedGroups);

        return extractedGroups;
    }

}
