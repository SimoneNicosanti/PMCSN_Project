package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.StatsQueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** A MultiServer is an object that can serve more than one group at a time */
public abstract class MultiServer extends StatsCenter {

    public MultiServer(String name, StatsQueueManager queueManager, int serverNumber, double avgServiceTime) {
        super(name, queueManager, serverNumber, avgServiceTime);
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return slotNumber - currentServingJobs.size() >= 1;
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        int freeSlots = slotNumber - currentServingJobs.size();
        return queueManager.extractFromQueues(freeSlots);
    }

    @Override
    public void endService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);

        this.manageEndService(endedJob);

        this.startService();
    }

    @Override
    protected void collectEndServiceStats(RiderGroup endedJob) {

        double jobServiceTime = this.retrieveServiceTime(endedJob);

        this.stats.addServiceTime(jobServiceTime);

        this.stats.endServiceUpdate(jobServiceTime, endedJob.getGroupSize());
    }

}
