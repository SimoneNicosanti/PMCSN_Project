package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;

public abstract class StatsCenter<T> implements CenterInterface<T> {

    protected CenterStats stats;
    private Map<T, Double> entranceTimeMap;
    private CenterInterface<T> center;

    protected StatsCenter(Center<T> center) {
        this.center = center;
        this.stats = new CenterStats();
        this.entranceTimeMap = new HashMap<>();
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
        entranceTimeMap.put(job, ClockHandler.getInstance().getClock());

        this.center.arrival(job);
    }

    /*
     * @return List<T> : List of jobs starting service with this call (may be one or
     * more)
     * 
     * @return Double : time for this service
     */
    public List<ServingGroup<T>> startService() {
        List<ServingGroup<T>> servingGroups = this.center.startService();

        this.collectServingStats(servingGroups);

        return this.center.startService();
    }

    /*
     * @param endedJobs : job ending service with this call
     */
    public void endService(T endedJob) {

        this.center.endService(endedJob);

        return;
    }

    protected abstract void collectServingStats(List<ServingGroup<T>> servingGroups);

}