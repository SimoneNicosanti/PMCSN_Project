package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction extends Center<RiderGroup> {

    protected double currentServiceTime;
    private double popularity;
    private double avgDuration;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats);
        this.currentServiceTime = 0.0;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
    }

    @Override
    public double startService() {
        this.serveJobs();

        this.currentServiceTime = RandomHandler.getInstance().getUniform(name, 0, 1);

        return this.currentServiceTime;
    }

    @Override
    public List<RiderGroup> endService() {
        for (RiderGroup riderGroup : currentServingJobs) {
            riderGroup.getGroupStats().incrementRidesInfo(this.name, currentServiceTime);
        }
        List<RiderGroup> terminatedJobs = currentServingJobs;
        this.currentServingJobs.clear();
        this.currentServiceTime = 0;

        return terminatedJobs;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

}
