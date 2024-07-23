package it.uniroma2.pmcsn.parks.model.queue;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class RestaurantQueue extends StatsQueue<RiderGroup> {

    public RestaurantQueue(Queue<RiderGroup> queue) {
        super(queue);
    }

    @Override
    protected void retrieveStats(RiderGroup group, double entranceTime, double waitingTime) {
        
        queueStats.updateStats(waitingTime);
    }

}
