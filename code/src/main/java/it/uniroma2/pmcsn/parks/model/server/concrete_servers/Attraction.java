package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.ImprovedAttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;

public class Attraction extends AbstractCenter {

    private double currentServiceTime;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new ImprovedAttractionQueueManager(), numberOfSeats, avgDuration, popularity);
        this.currentServiceTime = 0.0;
    }

    public Integer getQueueLenght(GroupPriority priority) {
        return this.queueManager.queueLength(priority);
    }

    @Override
    public Double getPopularity() {
        return this.popularity;
    }

    @Override
    public QueuePriority arrival(RiderGroup job) {
        return this.commonArrivalManagement(job);
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
        this.commonEndManagement(endedJob);

        // Attractions will start a new service when all the jobs have finished
        if (this.currentServingJobs.isEmpty()) {
            this.currentServiceTime = 0.0;
            // this.startService();
        }
    }

}
