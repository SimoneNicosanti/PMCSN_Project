package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
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

        // Priority groups has only a percentage of the attraction seats
        double priorityPercentage = Constants.PRIORITY_PERCENTAGE_PER_RIDE;
        int prioritySlots = (int) (freeSlots * priorityPercentage);

        // What if a priority group is bigger than the number of priority seat? Take
        // always at least one priority group
        if (priorityQueue.getNextSize() != 0) {
            // Get job from priority queue
            RiderGroup riderGroup = priorityQueue.dequeue();

            freeSlots -= riderGroup.getGroupSize();
            prioritySlots -= riderGroup.getGroupSize();
            extractedList.add(riderGroup);
        }

        while (true) {
            if (priorityQueue.getNextSize() <= prioritySlots && priorityQueue.getNextSize() != 0) {
                // Get job from priority queue
                RiderGroup riderGroup = priorityQueue.dequeue();
                if (riderGroup == null) {
                    // No one in the queue to serve --> Go to next queue
                    continue;
                }
                freeSlots -= riderGroup.getGroupSize();
                prioritySlots -= riderGroup.getGroupSize();
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

        throw new RuntimeException("Unkown priority.");
    }

    @Override
    public List<RiderGroup> dequeueAll() {
        List<RiderGroup> dequeuedList = new ArrayList<>();
        dequeuedList.addAll(priorityQueue.dequeueAll());
        dequeuedList.addAll(normalQueue.dequeueAll());
        return dequeuedList;
    }

}
