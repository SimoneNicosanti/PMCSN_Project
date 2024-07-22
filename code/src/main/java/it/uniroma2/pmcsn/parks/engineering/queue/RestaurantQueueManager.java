package it.uniroma2.pmcsn.parks.engineering.queue;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.AttractionQueue;

public class RestaurantQueueManager implements QueueManager<RiderGroup> {
    
    private Queue<RiderGroup> normalQueue ;

    public RestaurantQueueManager() {
        
        this.normalQueue = new AttractionQueue() ;
    }

    @Override
    public void addToQueues(RiderGroup item, double currentTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToQueues'");
    }

    @Override
    public List<RiderGroup> extractFromQueues(int slotNumber, double currentTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractFromQueues'");
    }

    @Override
    public boolean areQueuesEmpty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'areQueuesEmpty'");
    }
}
