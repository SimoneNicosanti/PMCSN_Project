package it.uniroma2.pmcsn.parks.model.stats;

public class CenterStats extends GeneralStats {
    // Number of jobs serverd by the center
    private long servedJobs;

    public CenterStats() {
        this.servedJobs = 0L;
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
