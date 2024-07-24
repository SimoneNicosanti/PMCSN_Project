package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;

public class StatsCenter implements CenterInterface<RiderGroup> {

    protected CenterStats stats;
    private CenterInterface<RiderGroup> center;

    public StatsCenter(Center<RiderGroup> center) {
        this.center = center;
        this.stats = new CenterStats();
    }

    public CenterStats getCenterStats() {
        return stats;
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return this.center.canServe(jobSize);
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
    public List<ServingGroup<RiderGroup>> startService() {
        List<ServingGroup<RiderGroup>> servingGroups = this.center.startService();

        this.collectStartServiceStats(servingGroups);

        return servingGroups;
    }

    /*
     * @param endedJobs : job ending service with this call
     */
    @Override
    public void endService(RiderGroup endedJob) {

        this.center.endService(endedJob);

        this.collectEndServiceStats(endedJob);

        return;
    }

    // Method useful for collecting new stats
    protected void collectEndServiceStats(RiderGroup endedJob) {
    }

    protected void collectStartServiceStats(List<ServingGroup<RiderGroup>> servingGroups) {

        for (ServingGroup<RiderGroup> group : servingGroups) {
            this.stats.addServingData(group.getServiceTime(), group.getGroup().getGroupSize());
        }
    }

    // Method useful for collecting new stats
    protected void collectArrivalStats(RiderGroup job) {
    }

}