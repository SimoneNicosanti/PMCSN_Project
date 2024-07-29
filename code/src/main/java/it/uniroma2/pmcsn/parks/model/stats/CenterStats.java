package it.uniroma2.pmcsn.parks.model.stats;

public class CenterStats {
    // The total service time of a center
    protected double serviceTime;
    // Number of jobs serverd by the center
    private long numberOfServedPerson;
    private long numberOfServedGroup;
    private long numberOfCompletedServices;

    public CenterStats() {
        this.serviceTime = 0;
        this.numberOfServedPerson = 0L;
    }

    public long getNumberOfServedPerson() {
        return this.numberOfServedPerson;
    }

    public long getNumberOfCompletedServices() {
        return numberOfCompletedServices;
    }

    public double getServiceTime() {
        return this.serviceTime;
    }

    public long getNumberOfServedGroup() {
        return this.numberOfServedGroup;
    }

    public void addServingData(double serviceTime, int servedJobs) {
        this.serviceTime += serviceTime;
        this.numberOfServedGroup++;
        this.numberOfServedPerson += servedJobs;
    }

    public void addServedGroup(int jobSize) {
        this.numberOfServedGroup++;
        this.numberOfServedPerson += jobSize;
    }

    public void addServiceTime(double serviceTime) {
        numberOfCompletedServices++;
        this.serviceTime += serviceTime;
    }

    public double getAvgServiceTime() {
        return this.serviceTime / this.numberOfServedPerson;
    }
}
