package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public class Restaurant extends Center<RiderGroup> {

    private double popularity;
    private double avgDuration;
    protected List<ServingGroup> currentServingJobs;

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
        return currentServingJobs != null && queueManager.areQueuesEmpty();
    }

    public boolean isCenterEmpty() {
        return !this.isServing() && this.queueManager.areQueuesEmpty();
    }

    private int getBusySlots() {
        int sum = 0;

        for (ServingGroup group : currentServingJobs) {
            sum += group.getGroup().getGroupSize();
        }

        return sum;
    }

    private int getFreeSlots() {
        return slotNumber - this.getBusySlots();
    }

    @Override
    public Pair<List<RiderGroup>, Double> startService() {

        List<RiderGroup> servingList = queueManager.extractFromQueues(this.getFreeSlots());

        // Save the current time for each group
        for (RiderGroup riderGroup : servingList) {
            this.currentServingJobs.add(new ServingGroup(riderGroup, ClockHandler.getInstance().getClock()));
        }

        double serviceTime = RandomHandler.getInstance().getRandom(this.name); // TODO find the correct distribution

        return Pair.of(servingList, serviceTime);
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
            if (!removeGroup(this.currentServingJobs, targetGroup)) {
                throw new RuntimeException();
            }
            
        }

        return;
    }

    /**
     * Return true if the group is successfully deleted, false otherwise (e.g. group
     * not found).
     */
    private boolean removeGroup(List<ServingGroup> list, RiderGroup targetGroup) {

        ServingGroup groupToDelete = null;

        for (ServingGroup servingGroup : list) {
            if (servingGroup.getGroup().equals(targetGroup)) {
                groupToDelete = servingGroup;
            }
        }

        if (groupToDelete == null) {
            return false;
        }

        return list.remove(groupToDelete);
    }
}
