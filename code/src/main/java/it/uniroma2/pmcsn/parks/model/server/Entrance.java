package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends Center<RiderGroup> {

    private RiderGroup currentServingJob;

    public Entrance(String name) {
        super(name, new EntranceQueueManager(), null);
        this.currentServingJob = null;
    }

    public double getArrivalInterval() {
        // TODO: not sure about distribution
        return RandomHandler.getInstance().getExponential(name, 0.1);
    }

    @Override
    public boolean isCenterEmpty() {
        return (currentServingJob == null) && queueManager.areQueuesEmpty();
    }

    @Override
    public Pair<List<RiderGroup>, Double> startService() {
        List<RiderGroup> jobsToServe = queueManager.extractFromQueues(null);

        if (jobsToServe.size() > 1) {
            throw new RuntimeException("Cannot extract more than a job from the queue");
        }

        this.currentServingJob = jobsToServe.get(0);

        int numOfRiders = this.currentServingJob.getGroupSize();

        // TODO Choose distribution
        double serviceTime = numOfRiders * RandomHandler.getInstance().getRandom(this.name);

        return Pair.of(jobsToServe, serviceTime);
    }

    @Override
    public void endService(List<RiderGroup> endedJobs) {
        // Entrance can handle only a job at a time
        if (endedJobs.size() != 1 || !endedJobs.get(0).equals(this.currentServingJob)) {
            throw new RuntimeException("Ended job is not the one being served");
        }

        this.currentServingJob = null;

        return;
    }

}
