package it.uniroma2.pmcsn.parks.model.stats;

public class CenterStats {
    // The total service time of a center
    protected double perPersonServiceTime;
    protected double perGroupServiceTime;
    protected double perCompletedServiceServiceTime;

    // Number of jobs serverd by the center
    private long numberOfServedPerson;
    private long numberOfServedGroup;
    private long numberOfCompletedServices;

    public CenterStats() {
        this.perPersonServiceTime = 0;
        this.numberOfServedPerson = 0L;
    }

    public long getNumberOfServedPerson() {
        return this.numberOfServedPerson;
    }

    public long getNumberOfCompletedServices() {
        return numberOfCompletedServices;
    }

    public double getPerPersonServiceTime() {
        return this.perPersonServiceTime;
    }

    public long getNumberOfServedGroup() {
        return this.numberOfServedGroup;
    }

    public void addServingData(double serviceTime, int personServed) {
        this.perGroupServiceTime += serviceTime;
        this.perPersonServiceTime += serviceTime * personServed;
        this.numberOfServedGroup++;
        this.numberOfServedPerson += personServed;
    }

    public void addServedGroup(int jobSize) {
        this.numberOfServedGroup++;
        this.numberOfServedPerson += jobSize;
    }

    public void addServiceTime(double serviceTime) {
        numberOfCompletedServices++;
        this.perPersonServiceTime += serviceTime;
    }

    public double getAvgServiceTimePerPerson() {
        return this.perPersonServiceTime / this.numberOfServedPerson;
    }

    public double getAvgServiceTimePerGroup() {
        return this.perGroupServiceTime / this.numberOfServedGroup;
    }

    public double getAvgServiceTimePerCompletedService() {
        return this.perCompletedServiceServiceTime / this.numberOfCompletedServices;
    }
}
