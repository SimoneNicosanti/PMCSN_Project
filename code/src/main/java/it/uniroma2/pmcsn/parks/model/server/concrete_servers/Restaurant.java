package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.MultiServer;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class Restaurant extends MultiServer {

    public Restaurant(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new RestaurantQueueManager(), numberOfSeats, avgDuration, popularity);
    }

    @Override
    public QueuePriority arrival(RiderGroup job) {
        return this.commonArrivalManagement(job);
    }

    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        if (Constants.VERIFICATION_MODE && job.getGroupSize() != 1)
            throw new RuntimeException();
        return RandomHandler.getInstance().getErlang(this.name, job.getGroupSize(), this.avgServiceTime);
    }

}
