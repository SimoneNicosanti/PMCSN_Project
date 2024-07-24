package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Restaurant extends Center {

    private double popularity;
    private double avgDuration;

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

    @Override
    public boolean canServe(Integer jobSize) {
        return getFreeSlots() >= jobSize;
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

    /**
     * End service for targeted groups.
     */
    @Override
    public void endService(RiderGroup endedJob) {
        if (currentServingJobs.isEmpty()) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }

        // Looking for the target group...
        if (!this.currentServingJobs.remove(endedJob)) {
            throw new RuntimeException("Group not found in the current serving jobs");
        }

        return;
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        return queueManager.extractFromQueues(this.getFreeSlots());
    }

    @Override
    protected Double getNewServiceTime(RiderGroup group) {
        return group.getGroupSize() * RandomHandler.getInstance().getUniform(this.name, 10, 20);
    }

    @Override
    public void arrival(RiderGroup job) {
        this.commonArrivalManagement(job);
    }

    @Override
    protected void terminateService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);

        RiderGroup group = ((RestaurantQueueManager) this.queueManager).getQueue().getNextJob();
        if (group != null) {
            this.startService();
        }
    }
}
