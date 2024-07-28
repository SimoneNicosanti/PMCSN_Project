package it.uniroma2.pmcsn.parks.model.stats;

public class CenterStats extends GeneralStats {
    // Number of jobs serverd by the center
    private long numberOfServedPeople;
    private long numberOfServedJobs;
    private long numberOfCompletedServices;

    public long getNumberOfCompletedServices() {
        return numberOfCompletedServices;
    }

    public CenterStats() {
        this.numberOfServedPeople = 0L;
    }

    public long getNumberOfServedPeople() {
        return this.numberOfServedPeople;
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
        numberOfCompletedServices++;
        this.serviceTime += serviceTime;
    }

    public double getAvgServiceTime() {
        return this.serviceTime / this.numberOfServedPeople;
    }

    public double getAvgQueueTime() {
        return this.queueTime / this.numberOfServedJobs;
    }

    public void addQueueTime(Double addQueueTime) {
        this.queueTime += addQueueTime;
    }
}
