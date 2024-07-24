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

        List<RiderGroup> list = new ArrayList<>();

        for (int i = 0; i < slotNumber; i++) {
            RiderGroup group = queue.dequeue();
            if (group == null) {
                break;
            }
            list.add(group);
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
