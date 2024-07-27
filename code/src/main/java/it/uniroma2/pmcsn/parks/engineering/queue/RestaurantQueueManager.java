package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.queue.RestaurantQueue;

public class RestaurantQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> normalQueue;

    public RestaurantQueueManager() {
        this.normalQueue = new RestaurantQueue(new FifoQueue(), QueuePriority.NORMAL);
    }

    @Override
    public List<Queue<RiderGroup>> getQueues() {
        return List.of(normalQueue);
    }

    @Override
    public void addToQueues(RiderGroup item) {
        normalQueue.enqueue(item);
    }

    @Override
    public boolean areQueuesEmpty() {
        return normalQueue.getNextSize() == 0;
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        List<RiderGroup> extractedGroups = new ArrayList<>();

        int freeSlots = slotNumber;
        while (true) {
            int nextGroupSize = normalQueue.getNextSize();
            if (nextGroupSize > 0 && nextGroupSize <= freeSlots) {
                extractedGroups.add(normalQueue.dequeue());
                freeSlots -= nextGroupSize;
            } else {
                break;
            }
        }

        return extractedGroups;
    }

    public Queue<RiderGroup> getQueue() {
        return normalQueue;
    }

}
