package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** A MultiServer is an object that can serve more than one group at a time */
public abstract class MultiServer extends AbstractCenter {

    public MultiServer(String name, QueueManager<RiderGroup> queueManager, int serverNumber, double avgServiceTime,
            double popularity) {
        super(name, queueManager, serverNumber, avgServiceTime, popularity);
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
        this.commonEndManagement(endedJob);

        // this.startService();
    }

    @Override
    public Double getPopularity() {
        return this.popularity;
    }

    @Override
    public Integer getQueueLenght(GroupPriority prio, Integer groupSize) {
        throw new UnsupportedOperationException("Unimplemented method 'getQueueLenght'");
    }

}
