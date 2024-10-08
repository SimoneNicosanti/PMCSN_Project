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

    private Integer normalQueueExtractionTryTimes;

    public AttractionQueueManager() {
        this.priorityQueue = new FifoQueue();
        this.normalQueue = new FifoQueue();
        this.normalQueueExtractionTryTimes = 0;
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer numberOfSeats) {

        List<RiderGroup> extractedList = new ArrayList<>();
        int freeSlots = numberOfSeats;

        // If there is a normal group blocking the queue because it is too big, then
        // we take it first
        if (this.normalQueueExtractionTryTimes > Constants.MAX_NORMAL_QUEUE_EXTRACTION_TRY_TIMES
                && normalQueue.getNextSize() != 0) {
            Integer firstNormalExtracted = extractFromOneQueue(normalQueue,
                    normalQueue.getNextSize(), extractedList);
            freeSlots -= firstNormalExtracted;

            this.normalQueueExtractionTryTimes = 0;
        }

        // What if a priority group is bigger than the number of priority seat? Take
        // always at least one priority group
        Integer priorityNextSize = priorityQueue.getNextSize();
        if (freeSlots > 0 && priorityNextSize <= freeSlots) {
            Integer priorityExtractedNum = extractFromOneQueue(priorityQueue,
                    priorityNextSize,
                    extractedList);
            if (priorityExtractedNum != priorityNextSize) {
                throw new RuntimeException("Extracted number is different than the expected size");
            }
            freeSlots = freeSlots - priorityExtractedNum;
        }

        // Priority groups has only a percentage of the attraction seats
        double priorityPercentage = Constants.PRIORITY_PERCENTAGE_PER_RIDE;
        int prioritySlots = (int) (freeSlots * priorityPercentage);

        // First extract all groups we can from the priority queue
        Integer priorityExtractedNum = extractFromOneQueue(priorityQueue,
                prioritySlots, extractedList);
        freeSlots = freeSlots - priorityExtractedNum;

        // Then we extract all groups we can from the normal queue
        Integer normalExtractedNum = extractFromOneQueue(normalQueue, freeSlots,
                extractedList);
        freeSlots = freeSlots - normalExtractedNum;
        if (normalExtractedNum == 0 && this.normalQueue.getNextSize() > 0) {
            // It means that there is a group blocking the normal queue
            this.normalQueueExtractionTryTimes++;
        } else if (normalExtractedNum > 0) {
            this.normalQueueExtractionTryTimes = 0;
        }

        // If there are other free seats, try to extract again from the priority
        // queue
        if (freeSlots > 0) {
            extractFromOneQueue(priorityQueue, freeSlots, extractedList);
        }

        return extractedList;
    }

    private Integer extractFromOneQueue(Queue<RiderGroup> queue, Integer numberOfSeats, List<RiderGroup> extracted) {
        Integer residualSeats = numberOfSeats;

        while (queue.getNextSize() <= residualSeats && queue.getNextSize() != 0) {
            // Get job from priority queue
            RiderGroup riderGroup = queue.dequeue();

            residualSeats -= riderGroup.getGroupSize();
            extracted.add(riderGroup);
        }

        Integer extractedSeats = numberOfSeats - residualSeats;
        return extractedSeats;
    }

    @Override
    public boolean areQueuesEmpty() {
        return priorityQueue.getNextSize() == 0 && normalQueue.getNextSize() == 0;
    }

    @Override
    public int queueLength(GroupPriority priority, Integer groupSize) {
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
