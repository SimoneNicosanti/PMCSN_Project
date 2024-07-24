package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

public interface QueueManager<T> {

    public void addToQueues(T item);

    public List<T> extractFromQueues(Integer slotNumber);

    public boolean areQueuesEmpty();

    public List<Queue<T>> getQueues();

}
