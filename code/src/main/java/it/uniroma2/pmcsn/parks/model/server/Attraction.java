package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction extends Center<RiderGroup> {

    private double popularity;
    private double avgDuration;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats);

        this.popularity = popularity;
        this.avgDuration = avgDuration;
    }

    @Override
    public double getDistribution() {
        int streamIndex = this.getStream(this.name);
        return RandomHandler.getInstance().getUniform(streamIndex, 0, 1);
    }

    @Override
    public List<RiderGroup> terminateJobs() {
        for (RiderGroup riderGroup : currentServingJobs) {
            riderGroup.getGroupStats().incrementRidesInfo(this.name, currentServiceTime);
        }
        return currentServingJobs;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

}
