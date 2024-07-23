package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends Center<RiderGroup> {

    private List<RiderGroup> currentServingJobs;

    public Entrance(String name, int slotNumber) {
        super(name, new EntranceQueueManager(), slotNumber);
        this.currentServingJobs = new ArrayList<>();
    }

    public double getArrivalInterval() {
        // TODO: not sure about distribution
        return RandomHandler.getInstance().getExponential(name, 0.1);
    }

    @Override
    public boolean isCenterEmpty() {
        return currentServingJobs.isEmpty() && queueManager.areQueuesEmpty();
    }

    @Override
    public List<ServingGroup<RiderGroup>> startService() {
        List<ServingGroup<RiderGroup>> servingGroups = new ArrayList<>();
        int freeSlots = slotNumber - currentServingJobs.size();
        List<RiderGroup> jobsToServe = queueManager.extractFromQueues(freeSlots);

        this.currentServingJobs.addAll(jobsToServe);

        for (RiderGroup group : jobsToServe) {
            int numOfRiders = group.getGroupSize();
            // TODO Choose distribution
            double serviceTime = numOfRiders * RandomHandler.getInstance().getRandom(this.name);
            servingGroups.add(new ServingGroup<RiderGroup>(group, serviceTime));
        }

        return servingGroups;
    }

    @Override
    public void endService(List<RiderGroup> endedJobs) {

        this.currentServingJobs.removeAll(endedJobs);

        return;
    }

}
