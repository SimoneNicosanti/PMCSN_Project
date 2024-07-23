package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction extends Center<RiderGroup> {

    protected double currentServiceTime;
    private double popularity;
    private double avgDuration;
    private ArrayList<RiderGroup> currentServingJobs;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats);
        this.currentServiceTime = 0.0;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
        this.currentServingJobs = new ArrayList<>();
    }

    @Override
    public Pair<List<RiderGroup>, Double> startService() {
        // Choose next groups to serve
        this.currentServingJobs.addAll(queueManager.extractFromQueues(this.slotNumber));

        // Choosing next service time for that attraction ride
        this.currentServiceTime = RandomHandler.getInstance().getUniform(name, 0, 1);

        return Pair.of(this.currentServingJobs, this.currentServiceTime);
    }

    public int getQueueLenght(GroupPriority priority) {
        return ((AttractionQueueManager) this.queueManager).queueLength(priority);
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

    @Override
    public void endService(List<RiderGroup> endedJobs) {
        for (RiderGroup riderGroup : currentServingJobs) {
            riderGroup.getGroupStats().incrementRidesInfo(this.name, this.currentServiceTime);
        }
        this.currentServingJobs.clear();
        this.currentServiceTime = 0;
    }

    @Override
    public boolean isCenterEmpty() {
        return queueManager.areQueuesEmpty() && this.currentServingJobs.isEmpty();
    }

}
