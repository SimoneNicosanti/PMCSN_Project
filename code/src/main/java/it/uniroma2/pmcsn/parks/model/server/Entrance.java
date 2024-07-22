package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends Center<RiderGroup> {
    private double serviceTime;

    public Entrance(String name) {
        super(name, new EntranceQueueManager(), Config.MAX_GROUP_SIZE);
    }

    public double getArrivalInterval() {
        // TODO: not sure about distribution
        return RandomHandler.getInstance().getExponential(name, 0.1);
    }

    @Override
    public double startService() {
        this.serveJobs();

        int numOfRiders = this.currentServingJobs.get(0).getGroupSize();
        this.serviceTime = numOfRiders * RandomHandler.getInstance().getUniform(name, 0, 1);

        return this.serviceTime;
    }

    @Override
    public List<RiderGroup> endService() {
        for (RiderGroup riderGroup : currentServingJobs) {
            riderGroup.getGroupStats().incrementRidesInfo(this.name, serviceTime);
        }
        List<RiderGroup> terminatedJobs = currentServingJobs;
        this.currentServingJobs.clear();
        this.serviceTime = 0;

        return terminatedJobs;
    }

}
