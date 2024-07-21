package it.uniroma2.pmcsn.parks.model;

import java.util.List;

public interface QueueManager<EnqueueItem> {

    public void addToQueues(EnqueueItem item, double currentTime) ;

    public List<EnqueueItem> extractFromQueues(int numberOfSeats, double currentTime) ;

}
