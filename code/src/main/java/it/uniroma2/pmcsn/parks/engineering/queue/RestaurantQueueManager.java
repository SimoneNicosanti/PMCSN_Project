package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class RestaurantQueueManager extends StatsQueueManager {

    private Queue<RiderGroup> queue;

    public RestaurantQueueManager() {
        this.queue = new FifoQueue();
        this.queueStatsMap.put(QueuePriority.NORMAL, new QueueStats(QueuePriority.NORMAL));
    }

    @Override
    public boolean areQueuesEmpty() {
        return queue.getNextSize() == 0;
    }

    public int queueLength() {
        return queue.queueLength();
    }

    @Override
    public void addToQueues(RiderGroup item) {
        this.commonStatsCollectionOnAdd(item);
        queue.enqueue(item);
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        List<RiderGroup> extractedGroups = new ArrayList<>();

        for (int i = 0; i < slotNumber; i++) {
            RiderGroup group = doDequeue(this.queue, QueuePriority.NORMAL);
            if (group == null) {
                break;
            }
            extractedGroups.add(group);
        }

        this.commonStatsCollectionOnExtract(extractedGroups);

        return extractedGroups;
    }

}
