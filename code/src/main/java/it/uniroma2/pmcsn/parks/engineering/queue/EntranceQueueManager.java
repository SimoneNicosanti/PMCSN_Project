package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.EntranceQueue;
import it.uniroma2.pmcsn.parks.model.queue.FifoQueue;

public class EntranceQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> queue;

    public EntranceQueueManager() {
        this.queue = new EntranceQueue(new FifoQueue());
    }

    @Override
    public void addToQueues(RiderGroup group) {
        queue.enqueue(group);
    }

    /**
     * @Params slotNumber: it represents the number of group that must be dequeued.
     * 
     * @Return: a list with a number of groups equal to slotNumber
     */
    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        if (slotNumber == null || slotNumber == 0) {
            throw new RuntimeException(
                    "The slot number shouldn't be null or 0");
        }

        List<RiderGroup> list = new ArrayList<>();

        for (int i = 0; i < slotNumber; i++) {
            list.add(queue.dequeue());
        }

        return list;
    }

    @Override
    public boolean areQueuesEmpty() {
        return queue.getNextSize() == 0;
    }

    public int queueLength() {
        return queue.queueLength();
    }

}
