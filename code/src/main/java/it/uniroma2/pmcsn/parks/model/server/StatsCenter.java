package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.StatsQueue;
import it.uniroma2.pmcsn.parks.model.stats.CenterStats;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

public class StatsCenter implements CenterInterface<RiderGroup> {

    private CenterStats stats;
    private Center center;
    private Map<RiderGroup, Double> startServingTimeMap;
    private List<StatsQueue<RiderGroup>> queues;

    public StatsCenter(Center center) {
        this.center = center;
        this.stats = new CenterStats();
        this.startServingTimeMap = new HashMap<>();
        this.queues = new ArrayList<>();

        List<Queue<RiderGroup>> centerQueues = this.center.getQueues();

        for (Queue<RiderGroup> queue : centerQueues) {
            if (!(queue instanceof StatsQueue<RiderGroup>))
                throw new RuntimeException("Expected a center with all stats queues");

            this.queues.add((StatsQueue<RiderGroup>) queue);
        }

    }

    public CenterStats getCenterStats() {
        stats.setQueueStats(getQueueStats());

        return stats;
    }

    private List<QueueStats> getQueueStats() {
        List<QueueStats> queueStats = new ArrayList<>();

        for (StatsQueue<RiderGroup> queue : this.queues) {
            queueStats.add(queue.getQueueStats());
        }

        return queueStats;
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
    public List<RiderGroup> startService() {

        // Start service
        List<RiderGroup> servingGroups = this.center.startService();

        // Collect data
        for (RiderGroup group : servingGroups) {
            startServingTimeMap.put(group, ClockHandler.getInstance().getClock());
        }

        return servingGroups;
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

    @Override
    public void setNextRoutingNode(RoutingNode<RiderGroup> nextRoutingNode) {
        this.center.setNextRoutingNode(nextRoutingNode);
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