package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.List;
import java.util.ArrayList;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class ImprovedAttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> priorityQueue;
    private Queue<RiderGroup> normalQueue;
    private Queue<RiderGroup> singleRiderQueue;

    public ImprovedAttractionQueueManager() {
        this.priorityQueue = new FifoQueue();
        this.normalQueue = new FifoQueue();
        this.singleRiderQueue = new FifoQueue();
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer numberOfSeats) {
        // TODO what if a group is too big for the attraction?

        List<RiderGroup> extractedList = new ArrayList<>();
        int freeSlots = numberOfSeats;

        // Priority groups has only a percentage of the attraction seats
        double priorityPercentage = Constants.PRIORITY_PERCENTAGE_PER_RIDE;
        int prioritySlots = (int) (freeSlots * priorityPercentage);

        double normalPercentage = Constants.NORMAL_PERCENTAGE_PER_RIDE;
        int normalSlots = ((int) (freeSlots * normalPercentage)) + prioritySlots;

        // What if a priority group is bigger than the number of priority seat? Take
        // always at least one priority group
        if (priorityQueue.getNextSize() > prioritySlots) {
            // Get job from priority queue
            RiderGroup riderGroup = priorityQueue.dequeue();

            freeSlots -= riderGroup.getGroupSize();
            normalSlots -= riderGroup.getGroupSize();
            prioritySlots -= riderGroup.getGroupSize();

            extractedList.add(riderGroup);
        }

        // What if a normal group is bigger than the number of normal seat? Take
        // some of the priority seats.
        if (normalQueue.getNextSize() > normalSlots) {
            // Get job from priority queue
            RiderGroup riderGroup = normalQueue.dequeue();

            freeSlots -= riderGroup.getGroupSize();
            prioritySlots -= riderGroup.getGroupSize();

            extractedList.add(riderGroup);
        }

        while (true) {
            RiderGroup riderGroup;

            if (priorityQueue.getNextSize() <= prioritySlots && priorityQueue.getNextSize() != 0) {
                // Get job from priority queue
                riderGroup = priorityQueue.dequeue();

                freeSlots -= riderGroup.getGroupSize();
                normalSlots -= riderGroup.getGroupSize();
                prioritySlots -= riderGroup.getGroupSize();
            } else if (normalQueue.getNextSize() <= normalSlots && normalQueue.getNextSize() != 0) {
                // Get job from normal queue
                riderGroup = normalQueue.dequeue();

                normalSlots -= riderGroup.getGroupSize();
                freeSlots -= riderGroup.getGroupSize();
            } else if (singleRiderQueue.getNextSize() <= freeSlots && singleRiderQueue.getNextSize() != 0) {
                // Get job from single rider queue
                riderGroup = singleRiderQueue.dequeue();

                freeSlots -= riderGroup.getGroupSize();
            } else {
                if (singleRiderQueue.getNextSize() == 0 && normalQueue.getNextSize() <= freeSlots
                        && normalQueue.getNextSize() != 0) {
                    // If there is no single rider, give all the free slots to the normal groups
                    normalSlots = freeSlots;
                    continue;
                }
                // No job is available for the number of free seats
                break;
            }
            extractedList.add(riderGroup);
        }

        return extractedList;
    }

    @Override
    public boolean areQueuesEmpty() {
        return priorityQueue.getNextSize() == 0 && normalQueue.getNextSize() == 0
                && singleRiderQueue.getNextSize() == 0;
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
        if (item.getPriority() == GroupPriority.PRIORITY) {
            priorityQueue.enqueue(item);
            return QueuePriority.PRIORITY;
        } else if (item.getPriority() == GroupPriority.NORMAL && item.getGroupSize() > 2) {
            normalQueue.enqueue(item);
            return QueuePriority.NORMAL;
        } else if (item.getPriority() == GroupPriority.NORMAL && item.getGroupSize() <= 2) {
            singleRiderQueue.enqueue(item);
            return QueuePriority.NORMAL;
        }

        throw new RuntimeException("Unkown priority.");
    }

    @Override
    public List<RiderGroup> dequeueAll() {
        List<RiderGroup> dequeuedList = new ArrayList<>();
        dequeuedList.addAll(priorityQueue.dequeueAll());
        dequeuedList.addAll(normalQueue.dequeueAll());
        dequeuedList.addAll(singleRiderQueue.dequeueAll());
        return dequeuedList;
    }

}
