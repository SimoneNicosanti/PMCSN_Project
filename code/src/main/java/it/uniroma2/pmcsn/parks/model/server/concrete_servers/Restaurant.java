package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.MultiServer;

public class Restaurant extends MultiServer {

    private double popularity;

    public Restaurant(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new RestaurantQueueManager(), numberOfSeats, avgDuration);
        this.popularity = popularity;
    }

    public double getPopularity() {
        return this.popularity;
    }

    @Override
    public void arrival(RiderGroup job) {
        this.manageArrival(job);
    }

    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        return RandomHandler.getInstance().getErlang(this.name, job.getGroupSize(), 1 / this.avgServiceTime);
    }

}
