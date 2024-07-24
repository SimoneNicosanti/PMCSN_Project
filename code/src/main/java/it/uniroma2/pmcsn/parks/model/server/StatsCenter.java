package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;

public abstract class StatsCenter<T> implements CenterInterface<T> {

    protected CenterStats stats;
    private CenterInterface<T> center;

    protected StatsCenter(Center<T> center) {
        this.center = center;
        this.stats = new CenterStats();
    }

    public CenterStats getCenterStats() {
        return stats;
    }

    public String getName() {
        return null;
    }

    public boolean isCenterEmpty() {
        return false;
    }

    /*
     * Add a job to the center queue.
     * 
     * @param job : job to enqueue with this call
     */
    public void arrival(T job) {
        this.collectArrivalStats(job);

        this.center.arrival(job);
    }

    /*
     * @return List<T> : List of jobs starting service with this call (may be one or
     * more)
     */
    public List<ServingGroup<T>> startService() {
        List<ServingGroup<T>> servingGroups = this.center.startService();

        this.collectStartServiceStats(servingGroups);

        return servingGroups;
    }

    /*
     * @param endedJobs : job ending service with this call
     */
    public void endService(T endedJob) {

        this.center.endService(endedJob);

        this.collectEndServiceStats(endedJob);

        return;
    }

    // Method useful for adding new
    protected void collectEndServiceStats(T endedJob) {
    }

    protected void collectStartServiceStats(List<ServingGroup<T>> servingGroups) {

    }

    protected void collectArrivalStats(T job) {
    }

}