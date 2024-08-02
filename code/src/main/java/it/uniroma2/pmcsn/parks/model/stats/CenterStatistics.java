package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public class CenterStatistics {
    // The total service time of a center
    protected double perPersonServiceTime;
    protected double perGroupServiceTime;
    protected double perCompletedServiceServiceTime;

    // Number of jobs serverd by the center
    private long numberOfServedPerson;
    private long numberOfServedGroup;
    private long numberOfCompletedServices;

    // Time-averaged stats
    private double groupsArea;
    private double peopleArea;

    private double previousEventTime;

    public CenterStatistics() {
        this.perPersonServiceTime = 0;
        this.perGroupServiceTime = 0;
        this.perCompletedServiceServiceTime = 0;

        this.numberOfServedPerson = 0L;
        this.numberOfServedGroup = 0L;
        this.numberOfCompletedServices = 0L;

        this.groupsArea = 0.0;
        this.peopleArea = 0.0;

        this.previousEventTime = ClockHandler.getInstance().getClock();

    }

    public double getAvgGroupQueueTimeByArea() {
        return getAvgGroupWaitByArea() - getAvgServiceTimePerGroup();
    }

    public double getAvgPersonQueueTimeByArea() {
        return getAvgPersonWaitByArea() - getAvgServiceTimePerPerson();
    }

    public double getAvgPersonWaitByArea() {
        if (numberOfServedPerson == 0)
            return 0;
        return peopleArea / numberOfServedPerson;
    }

    public double getAvgGroupWaitByArea() {
        if (numberOfServedPerson == 0)
            return 0;
        return groupsArea / numberOfServedGroup;
    }

    public double getAvgNumberOfPersonInTheSystem() {
        return peopleArea / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfGroupInTheSystem() {
        return groupsArea / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfPersonInTheQueue() {
        double area = peopleArea - perPersonServiceTime;
        return area / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfGroupInTheQueue() {
        double area = groupsArea - perGroupServiceTime;
        return area / ClockHandler.getInstance().getClock();
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

    public void updateAreas(long groups, long people) {
        double currentEventTime = ClockHandler.getInstance().getClock();

        assert groups < 0 || people < 0;
        assert currentEventTime - previousEventTime < 0;

        groupsArea += (currentEventTime - previousEventTime) * groups;
        peopleArea += (currentEventTime - previousEventTime) * people;

        previousEventTime = currentEventTime;
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
