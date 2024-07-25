package it.uniroma2.pmcsn.parks.model.stats;

import java.util.List;

public class CenterStats extends GeneralStats {
    // Number of jobs serverd by the center
    private long servedJobs;
    private List<QueueStats> queueStatsList;

    public CenterStats() {
        this.servedJobs = 0L;
    }

    public void setQueueStats(List<QueueStats> queueStats) {
        this.queueStatsList = queueStats;
    }

    public List<QueueStats> getQueueStats() {
        return this.queueStatsList;
    }

    public long getServedJobs() {
        return this.servedJobs;
    }

    public void addServingData(double serviceTime, int servedJobs) {
        this.serviceTime += serviceTime;
        this.servedJobs += servedJobs;
    }

    public double getAvgServiceTime() {
        return this.serviceTime / this.servedJobs;
    }
}
