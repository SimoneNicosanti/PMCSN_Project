package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.AttractionQueue;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;

public class AttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> priorityQueue;
    private Queue<RiderGroup> normalQueue;

    public AttractionQueueManager() {
        this.priorityQueue = new AttractionQueue(new FifoQueue());
        this.normalQueue = new AttractionQueue(new FifoQueue());
    }

    @Override
    public void addToQueues(RiderGroup group) {
        switch (group.getPriority()) {
            case PRIORITY:
                priorityQueue.enqueue(group);
                break;
            case NORMAL:
                normalQueue.enqueue(group);
                break;
        }
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

}
