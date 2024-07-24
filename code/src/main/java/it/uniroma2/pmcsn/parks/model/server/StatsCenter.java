package it.uniroma2.pmcsn.parks.model.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;

public class StatsCenter implements CenterInterface<RiderGroup> {

    protected CenterStats stats;
    private CenterInterface<RiderGroup> center;
    private Map<RiderGroup, Double> startServingTimeMap;

    public StatsCenter(CenterInterface<RiderGroup> center) {
        this.center = center;
        this.stats = new CenterStats();
        this.startServingTimeMap = new HashMap<>();
    }

    public CenterStats getCenterStats() {
        return stats;
    }

    @Override
    public String getName() {
        return this.center.getName();
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSlots) {
        return this.center.isQueueEmptyAndCanServe(jobSlots);
    }

    /*
     * Add a job to the center queue.
     * 
     * @param job : job to enqueue with this call
     */
    @Override
    public void arrival(RiderGroup job) {
        this.collectArrivalStats(job);

        this.center.arrival(job);
    }

    /*
     * @return List<T> : List of jobs starting service with this call (may be one or
     * more)
     */
    @Override
    protected void startService() {
        List<RiderGroup> servingGroups = this.center.startService();

        for (RiderGroup group : servingGroups) {
            startServingTimeMap.put(group, ClockHandler.getInstance().getClock());
        }
    }

    /*
     * @param endedJobs : job ending service with this call
     */
    @Override
    public void endService(RiderGroup endedJob) {
        double servingTime = ClockHandler.getInstance().getClock() - startServingTimeMap.get(endedJob);

        this.collectEndServiceStats(endedJob, servingTime);

        this.center.endService(endedJob);

        return;
    }

    // Method useful for collecting new stats
    protected void collectEndServiceStats(RiderGroup endedJob, double serviceTime) {
        if (this.center instanceof Attraction)
            endedJob.getGroupStats().incrementRidesInfo(center.getName(), serviceTime);

        this.stats.addServingData(serviceTime, endedJob.getGroupSize());
    }

    // Method useful for collecting new stats
    protected void collectArrivalStats(RiderGroup job) {
    }

}