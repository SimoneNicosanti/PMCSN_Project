package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public interface QueueManager<T> {

    public QueuePriority addToQueues(T item);

    public List<T> extractFromQueues(Integer slotNumber);

    public boolean areQueuesEmpty();

    public int queueLength(GroupPriority priority, Integer groupSize);

    public List<T> dequeueAll();

}
