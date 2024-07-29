package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public interface QueueManager<T> {

    public void addToQueues(T item);

    public List<T> extractFromQueues(Integer slotNumber);

    public boolean areQueuesEmpty();

    public List<QueueStats> getAllQueueStats();

    public QueueStats getGeneralQueueStats();

}
