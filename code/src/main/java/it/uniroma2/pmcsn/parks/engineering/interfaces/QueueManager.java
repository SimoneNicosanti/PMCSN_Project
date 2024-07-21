package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

public interface QueueManager<T> {

    public void addToQueues(T item, double currentTime) ;

    public List<T> extractFromQueues(int numberOfSeats, double currentTime) ;

    public boolean areQueuesEmpty();

}
