package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class RestaurantQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> queue;

    public RestaurantQueueManager() {
        this.queue = new FifoQueue();
    }

    @Override
    public boolean areQueuesEmpty() {
        return queue.getNextSize() == 0;
    }

    public int queueLength() {
        return queue.queueLength();
    }

    @Override
    public QueuePriority addToQueues(RiderGroup item) {
        queue.enqueue(item);
        return QueuePriority.NORMAL;
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        List<RiderGroup> extractedGroups = new ArrayList<>();

        for (int i = 0; i < slotNumber; i++) {
            RiderGroup group = this.queue.dequeue();
            if (group == null) {
                break;
            }
            extractedGroups.add(group);
        }

        return extractedGroups;
    }

    @Override
    public int queueLength(GroupPriority priority) {
        return this.queue.queueLength();
    }

    @Override
    public List<RiderGroup> dequeueAll() {
        return queue.dequeueAll();
    }

}
