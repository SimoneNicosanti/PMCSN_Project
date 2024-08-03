package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class AttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> priorityQueue;
    private Queue<RiderGroup> normalQueue;

    public AttractionQueueManager() {
        this.priorityQueue = new FifoQueue();
        this.normalQueue = new FifoQueue();
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer numberOfSeats) {
        List<RiderGroup> extractedList = new ArrayList<>();
        int freeSlots = numberOfSeats;
        while (true) {
            if (priorityQueue.getNextSize() <= freeSlots && priorityQueue.getNextSize() != 0) {
                // Get job from priority queue
                RiderGroup riderGroup = priorityQueue.dequeue();
                if (riderGroup == null) {
                    // No one in the queue to serve --> Go to next queue
                    continue;
                }
                freeSlots -= riderGroup.getGroupSize();
                extractedList.add(riderGroup);
            } else if (normalQueue.getNextSize() <= freeSlots && normalQueue.getNextSize() != 0) {
                // Get job from normal queue
                RiderGroup riderGroup = normalQueue.dequeue();
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

        return extractedList;
    }

    @Override
    public boolean areQueuesEmpty() {
        return priorityQueue.getNextSize() == 0 && normalQueue.getNextSize() == 0;
    }

    @Override
    public int queueLength(GroupPriority priority) {
        int queueLength = 0;
        switch (priority) {
            case PRIORITY:
                queueLength = priorityQueue.queueLength();
                break;
            case NORMAL:
                queueLength = priorityQueue.queueLength() + normalQueue.queueLength();
                break;
        }
        return queueLength;
    }

    @Override
    public QueuePriority addToQueues(RiderGroup item) {

        switch (item.getPriority()) {
            case PRIORITY:
                priorityQueue.enqueue(item);
                return QueuePriority.PRIORITY;
            case NORMAL:
                normalQueue.enqueue(item);
                return QueuePriority.NORMAL;
        }
        return QueuePriority.NORMAL;
    }

}
