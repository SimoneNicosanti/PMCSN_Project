package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;

public class Attraction extends StatsCenter {

    private double currentServiceTime;
    private double popularity;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new AttractionQueueManager(), numberOfSeats, avgDuration);
        this.currentServiceTime = 0.0;
        this.popularity = popularity;
    }

    public int getQueueLenght(GroupPriority priority) {
        return ((AttractionQueueManager) this.queueManager).queueLength(priority);
    }

    public double getPopularity() {
        return this.popularity;
    }

    @Override
    public void arrival(RiderGroup job) {
        this.manageArrival(job);
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return this.currentServingJobs.isEmpty();
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        return queueManager.extractFromQueues(this.slotNumber);
    }

    @Override // Override to do verify
    protected Double getNewServiceTime(RiderGroup group) {
        if (this.currentServiceTime == 0.0) {
            this.currentServiceTime = RandomHandler.getInstance().getUniform(name, this.avgServiceTime - 0.5,
                    this.avgServiceTime + 0.5);
        }
        return this.currentServiceTime;
    }

    @Override
    public void endService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);

        this.manageEndService(endedJob);

        if (currentServingJobs.isEmpty()) {
            this.currentServiceTime = 0.0;
            this.startService();
        }
    }

    @Override // Override to do verify
    protected void collectEndServiceStats(RiderGroup endedJob) {
        double jobServiceTime = this.retrieveServiceTime(endedJob);

        endedJob.getGroupStats().incrementRidesInfo(this.getName(), jobServiceTime);

        if (this.startServingTimeMap.isEmpty()) {
            this.stats.addServiceTime(jobServiceTime);
        }

        this.stats.endServiceUpdate(jobServiceTime, endedJob.getGroupSize());
    }

}
