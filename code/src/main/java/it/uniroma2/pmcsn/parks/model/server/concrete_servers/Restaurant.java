package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.RestaurantQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;

public class Restaurant extends StatsCenter {

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

    @Override
    public void endService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);
        this.startService();

        this.manageEndService(endedJob);
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        return queueManager.extractFromQueues(this.getFreeSlots());
    }

    @Override
    protected Double getNewServiceTime(RiderGroup group) {
        // TODO Choose distribution
        return group.getGroupSize() * RandomHandler.getInstance().getUniform(this.name, 10, 20);
    }

    @Override
    public void arrival(RiderGroup job) {
        this.manageArrival(job);
    }

    @Override
    protected void collectEndServiceStats(RiderGroup endedJob) {

        double jobServiceTime = this.getServiceTime(endedJob);

        this.stats.addServiceTime(jobServiceTime);

        this.stats.addServedGroup(endedJob.getGroupSize());
    }

}
