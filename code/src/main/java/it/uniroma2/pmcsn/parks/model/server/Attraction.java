package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public class Attraction extends Center<RiderGroup> {

    private double currentServiceTime;
    private double popularity;
    private double avgDuration;
    private List<RiderGroup> currentServingJobs;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats);
        this.currentServiceTime = 0.0;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
        this.currentServingJobs = new ArrayList<>();
    }

    @Override
    public List<ServingGroup<RiderGroup>> startService() {
        // Choose next groups to serve
        List<RiderGroup> startedGroups = queueManager.extractFromQueues(this.slotNumber);
        this.currentServingJobs.addAll(startedGroups);

        // Choosing next service time for that attraction ride
        this.currentServiceTime = RandomHandler.getInstance().getUniform(name, avgDuration - 0.5, avgDuration + 0.5);

        List<ServingGroup<RiderGroup>> returnList = new ArrayList<>();
        for (RiderGroup riderGroup : startedGroups) {
            returnList.add(new ServingGroup<RiderGroup>(riderGroup, this.currentServiceTime));
        }

        return returnList;
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
    public void endService(RiderGroup endedJob) {
        endedJob.getGroupStats().incrementRidesInfo(this.name, this.currentServiceTime);
        this.currentServingJobs.remove(endedJob) ;
        if (this.currentServingJobs.isEmpty()) {
            this.currentServiceTime = 0;
        }
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return this.currentServingJobs.isEmpty();
    }

}
