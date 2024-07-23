package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends Center<RiderGroup> {
    private double serviceTime;
    protected RiderGroup currentServingJob;

    public Entrance(String name) {
        super(name, new EntranceQueueManager(), Config.MAX_GROUP_SIZE);
    }

    public double getArrivalInterval() {
        // TODO: not sure about distribution
        return RandomHandler.getInstance().getExponential(name, 0.1);
    }

    @Override
    public Pair<List<RiderGroup>, Double> startService() {
        List<RiderGroup> jobsToServe = queueManager.extractFromQueues(null);

        if (jobsToServe.size() > 1) {
            throw new RuntimeException();
        }

        this.currentServingJob = jobsToServe.get(0);

        int numOfRiders = this.currentServingJob.getGroupSize();

        this.serviceTime = numOfRiders * RandomHandler.getInstance().getRandom(this.name); // TODO add the correct
                                                                                           // distribution

        return Pair.of(jobsToServe, serviceTime);
    }

    @Override
    public List<RiderGroup> endService() {
        this.currentServingJob.getGroupStats().incrementRidesInfo(this.name, serviceTime);
        this.currentServingJob = null;
        this.serviceTime = 0;

        return List.of(this.currentServingJob);
    }

}
