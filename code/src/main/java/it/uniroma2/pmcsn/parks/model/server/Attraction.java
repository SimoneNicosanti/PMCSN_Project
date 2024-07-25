package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction extends StatsCenter {

    private double currentServiceTime;
    private double popularity;
    private double avgDuration;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats);
        this.currentServiceTime = 0.0;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
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
    protected void terminateService(RiderGroup endedJob) {

        this.currentServingJobs.remove(endedJob);

        if (currentServingJobs.isEmpty()) {
            this.currentServiceTime = 0.0;
            this.startService();
        }
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return this.currentServingJobs.isEmpty();
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {

        return queueManager.extractFromQueues(this.slotNumber);
    }

    @Override
    protected Double getNewServiceTime(RiderGroup group) {
        if (this.currentServiceTime == 0.0) {
            this.currentServiceTime = RandomHandler.getInstance().getUniform(name, avgDuration - 0.5,
                    avgDuration + 0.5);
        }
        return this.currentServiceTime;
    }

    @Override
    public void doArrival(RiderGroup job) {

    }

    @Override
    public void doEndService(RiderGroup endedJob) {
        this.commonEndManagement(endedJob);
    }

}
