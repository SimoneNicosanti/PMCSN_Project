package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.AttractionQueue;
import it.uniroma2.pmcsn.parks.model.queue.EnqueuedItem;

public class EntranceQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> queue;

    public EntranceQueueManager() {
        this.queue = new AttractionQueue();
    }

    @Override
    public void addToQueues(RiderGroup group) {
        if (group.getGroupSize() > Config.MAX_GROUP_SIZE) {
            throw new RuntimeException("Group size exceeds the maximum group size");
        }
        queue.enqueue(group);
    }

    @Override
    public List<RiderGroup> extractFromQueues(Integer slotNumber) {
        double currentTime = ClockHandler.getInstance().getClock();
        // If there are groups in the queue, they surely do not exceed the maximum group
        // size
        RiderGroup riderGroup = queue.dequeue();
        return new ArrayList<>(List.of(riderGroup));
    }

    @Override
    public boolean areQueuesEmpty() {
        return queue.getNextSize() == 0;
    }

    public int queueLength() {
        return queue.queueLength();
    }

}
