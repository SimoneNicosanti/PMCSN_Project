package it.uniroma2.pmcsn.parks.model.stats;

import java.util.List;

public class CenterStats extends GeneralStats {
    // Number of jobs serverd by the center
    private long numberOfServedPeople;
    private long numberOfServedJobs;
    private long numberOfCompletedServices;

    public long getNumberOfCompletedServices() {
        return numberOfCompletedServices;
    }

    private List<QueueStats> queueStatsList;

    public CenterStats() {
        this.numberOfServedPeople = 0L;
    }

    public void setQueueStats(List<QueueStats> queueStats) {
        this.queueStatsList = queueStats;
    }

    public List<QueueStats> getQueueStats() {
        return this.queueStatsList;
    }

    public long getNumberOfServedPeople() {
        return this.numberOfServedPeople;
    }

    public void addCompletedService() {
        numberOfCompletedServices++;
    }

    public void addServingData(double serviceTime, int servedJobs) {
        this.serviceTime += serviceTime;
        this.numberOfServedJobs++;
        this.numberOfServedPeople += servedJobs;
    }

    public void addServedGroup(int jobSize) {
        this.numberOfServedJobs++;
        this.numberOfServedPeople += jobSize;
    }

    public void addServiceTime(double serviceTime) {
        this.serviceTime += serviceTime;
    }

    public double getAvgServiceTime() {
        return this.serviceTime / this.numberOfServedPeople;
    }
}
