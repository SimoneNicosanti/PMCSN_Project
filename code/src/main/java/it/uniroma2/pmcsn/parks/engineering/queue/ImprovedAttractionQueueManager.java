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

public class ImprovedAttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> priorityQueue;
    private Queue<RiderGroup> normalQueue;
    private Queue<RiderGroup> smallRiderQueue;

    private Integer normalQueueExtractionTryTimes;

    public ImprovedAttractionQueueManager() {
        this.priorityQueue = new FifoQueue();
        this.normalQueue = new FifoQueue();
        this.smallRiderQueue = new FifoQueue();

        this.normalQueueExtractionTryTimes = 0;
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer numberOfSeats) {
        List<RiderGroup> extractedList = new ArrayList<>();
        int freeSlots = numberOfSeats;

        // If there is a normal group blocking the queue because it is too big, then we
        // take it first
        if (this.normalQueueExtractionTryTimes > Constants.MAX_NORMAL_QUEUE_EXTRACTION_TRY_TIMES
                && normalQueue.getNextSize() != 0) {
            Integer firstNormalExtracted = extractFromOneQueue(normalQueue,
                    normalQueue.getNextSize(), extractedList);
            freeSlots -= firstNormalExtracted;

            this.normalQueueExtractionTryTimes = 0;
        }

        // Small groups have a percentage of the attraction seats
        int smallSlots = (int) (freeSlots * Constants.SMALL_GROUP_PERCENTAGE_PER_RIDE);

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
            // prioritySlots -= priorityExtractedNum;
        }

        // Priority groups has only a percentage of the attraction seats
        int prioritySlots = (int) (freeSlots * Constants.PRIORITY_PERCENTAGE_PER_RIDE);

        // First extract all groups we can from the priority queue
        Integer priorityExtractedNum = extractFromOneQueue(priorityQueue,
                prioritySlots, extractedList);
        freeSlots = freeSlots - priorityExtractedNum;

        // Then we extract groups for normal priority, leaving some seats for small
        // groups
        Integer normalReservedSeats = freeSlots - smallSlots;

        Integer normalExtractedNum = extractFromOneQueue(normalQueue,
                normalReservedSeats, extractedList);
        freeSlots = freeSlots - normalExtractedNum;
        if (normalExtractedNum == 0 && this.normalQueue.getNextSize() > 0) {
            // It means that there is a group blocking the normal queue
            this.normalQueueExtractionTryTimes++;
        } else if (normalExtractedNum > 0) {
            this.normalQueueExtractionTryTimes = 0;
        }

        // Then we extract from the small rider queue
        Integer extractedSmallNum = extractFromOneQueue(smallRiderQueue, freeSlots,
                extractedList);
        freeSlots = freeSlots - extractedSmallNum;

        // If there are other free seats, try to extract again from the priority
        // queue
        if (freeSlots > 0) {
            freeSlots = freeSlots - extractFromOneQueue(priorityQueue, freeSlots,
                    extractedList);
        }

        // If there are other free seats, try to extract again from the normal queue
        if (freeSlots > 0) {
            normalExtractedNum = extractFromOneQueue(normalQueue, freeSlots,
                    extractedList);
            freeSlots = freeSlots - normalExtractedNum;

            if (normalExtractedNum > 0) {
                this.normalQueueExtractionTryTimes = 0;
            }
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

        return numberOfSeats - residualSeats;
    }

    @Override
    public boolean areQueuesEmpty() {
        return priorityQueue.getNextSize() == 0 && normalQueue.getNextSize() == 0
                && smallRiderQueue.getNextSize() == 0;
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

        if (priority == GroupPriority.NORMAL
                && groupSize <= Constants.SMALL_GROUP_LIMIT_SIZE) {
            queueLength = smallRiderQueue.queueLength();
        }

        return queueLength;
    }

    @Override
    public QueuePriority addToQueues(RiderGroup item) {
        if (item.getPriority() == GroupPriority.PRIORITY) {
            priorityQueue.enqueue(item);
            return QueuePriority.PRIORITY;
        } else if (item.getPriority() == GroupPriority.NORMAL
                && item.getGroupSize() > Constants.SMALL_GROUP_LIMIT_SIZE) {
            normalQueue.enqueue(item);
            return QueuePriority.NORMAL;
        } else if (item.getPriority() == GroupPriority.NORMAL
                && item.getGroupSize() <= Constants.SMALL_GROUP_LIMIT_SIZE) {
            smallRiderQueue.enqueue(item);
            return QueuePriority.SMALL;
        }

        throw new RuntimeException("Unkown priority.");
    }

    @Override
    public List<RiderGroup> dequeueAll() {
        List<RiderGroup> dequeuedList = new ArrayList<>();
        dequeuedList.addAll(priorityQueue.dequeueAll());
        dequeuedList.addAll(normalQueue.dequeueAll());
        dequeuedList.addAll(smallRiderQueue.dequeueAll());
        return dequeuedList;
    }

}
