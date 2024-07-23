package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public class Restaurant extends Center<RiderGroup> {

    private double popularity;
    private double avgDuration;
    protected List<RiderGroup> currentServingJobs;

    public Restaurant(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, new RestaurantQueueManager(), numberOfSeats);
        this.popularity = popularity;
        this.avgDuration = avgDuration;
        this.currentServingJobs = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

    public boolean isServing() {
        return !currentServingJobs.isEmpty();
    }

    public boolean isCenterEmpty() {
        return !this.isServing() && this.queueManager.areQueuesEmpty();
    }

    private int getBusySlots() {
        int sum = 0;

        for (RiderGroup group : currentServingJobs) {
            sum += group.getGroupSize();
        }

        return sum;
    }

    private int getFreeSlots() {
        return slotNumber - this.getBusySlots();
    }

    @Override
    public List<ServingGroup<RiderGroup>> startService() {

        List<ServingGroup<RiderGroup>> newServingGroups = new ArrayList<>();

        List<RiderGroup> servingList = queueManager.extractFromQueues(this.getFreeSlots());

        this.currentServingJobs.addAll(servingList);

        // Save the current time for each group
        for (RiderGroup riderGroup : servingList) {
            // TODO: Choose distribution. As in the entrance, it might make sense to weight
            // for the group size
            double serviceTime = riderGroup.getGroupSize() * RandomHandler.getInstance().getRandom(this.name);
            newServingGroups.add(new ServingGroup<RiderGroup>(riderGroup, serviceTime));
        }

        return newServingGroups;
    }

    /**
     * End service for targeted groups.
     */
    @Override
    public void endService(List<RiderGroup> targetGroups) {
        if (currentServingJobs.isEmpty()) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }

        for (RiderGroup targetGroup : targetGroups) {
            // Looking for the target group...
            if (!this.currentServingJobs.remove(targetGroup)) {
                throw new RuntimeException("Group not found in the current serving jobs");
            }
        }

        return;
    }
}
