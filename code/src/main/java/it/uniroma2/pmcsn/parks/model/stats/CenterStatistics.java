package it.uniroma2.pmcsn.parks.model.stats;

public class CenterStatistics {
    // The total service time of a center
    protected double perPersonServiceTime;
    protected double perGroupServiceTime;
    protected double perCompletedServiceServiceTime;

    // Number of jobs serverd by the center
    private long numberOfServedPerson;
    private long numberOfServedGroup;
    private long numberOfCompletedServices;

    public CenterStatistics() {
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

    public void endServiceUpdate(double serviceTime, int personServed) {
        this.perGroupServiceTime += serviceTime;
        this.perPersonServiceTime += serviceTime * personServed;
        this.numberOfServedGroup++;
        this.numberOfServedPerson += personServed;
    }

    public void addServiceTime(double serviceTime) {
        numberOfCompletedServices++;
        this.perCompletedServiceServiceTime += serviceTime;
    }

    public double getAvgServiceTimePerPerson() {
        if (numberOfServedPerson == 0)
            return 0;
        return this.perPersonServiceTime / this.numberOfServedPerson;
    }

    public double getAvgServiceTimePerGroup() {
        if (numberOfServedGroup == 0)
            return 0;
        return this.perGroupServiceTime / this.numberOfServedGroup;
    }

    public double getAvgServiceTimePerCompletedService() {
        if (numberOfCompletedServices == 0)
            return 0;
        return this.perCompletedServiceServiceTime / this.numberOfCompletedServices;
    }
}
