package it.uniroma2.pmcsn.parks.model.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class CenterStatistics {
    // The total service time of a center
    protected double perPersonServiceTime;
    protected double perGroupServiceTime;
    protected double perCompletedServiceServiceTime;

    // Number of jobs serverd by the center
    private long numberOfServedPerson;
    private Map<QueuePriority, Long> personPerPriority; // Given the priority, return the number of person served
    private long numberOfServedGroup;
    private Map<QueuePriority, Long> groupPerPriority; // Given the priority, return the number of group served
    private long numberOfCompletedServices;

    // Time-averaged stats
    private double groupsArea;
    private double peopleArea;

    private double previousEventTime;

    // Queue stats
    private List<QueueStats> queueStatsList;
    private QueueStats aggregatedQueueStats;

    // NEW STATS
    private AreaStats serviceAreaGroup;
    private AreaStats serviceAreaPeople;

    private AreaStats queueAreaGroup;
    private AreaStats queueAreaPeople;
    private Map<QueuePriority, AreaStats> queueAreaPerPrioGroup;
    private Map<QueuePriority, AreaStats> queueAreaPerPrioPeople;

    public CenterStatistics() {
        this.perPersonServiceTime = 0;
        this.perGroupServiceTime = 0;
        this.perCompletedServiceServiceTime = 0;

        this.numberOfServedPerson = 0L;
        this.numberOfServedGroup = 0L;
        this.numberOfCompletedServices = 0L;

        this.groupsArea = 0.0;
        this.peopleArea = 0.0;

        this.groupPerPriority = new HashMap<>();
        this.personPerPriority = new HashMap<>();

        for (QueuePriority priority : QueuePriority.values()) {
            groupPerPriority.put(priority, 0L);
            personPerPriority.put(priority, 0L);
        }

        this.previousEventTime = 0.0;

        // NEW STATS
        this.serviceAreaGroup = new AreaStats();
        this.serviceAreaPeople = new AreaStats();

        this.queueAreaGroup = new AreaStats();
        this.queueAreaPeople = new AreaStats();

        this.queueAreaPerPrioGroup = new HashMap<>();
        this.queueAreaPerPrioPeople = new HashMap<>();
    }

    public void setQueueStats(List<QueueStats> queueStatsList) {
        this.queueStatsList = queueStatsList;
    }

    public List<QueueStats> getQueueStats() {
        return this.queueStatsList;
    }

    public double getAvgGroupQueueTimeByArea() {
        long groups = aggregatedQueueStats.getNumberOfGroupEnqueued();
        if (groups == 0)
            return 0;
        double area = groupsArea - perGroupServiceTime;
        return area / groups;
    }

    public double getAvgPersonQueueTimeByArea() {
        long people = aggregatedQueueStats.getNumberOfPersonEnqueued();
        if (people == 0)
            return 0;
        double area = peopleArea - perPersonServiceTime;
        return area / people;
    }

    public double getAvgNumberOfPersonInTheSystem() {
        return peopleArea / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfGroupInTheSystem() {
        return groupsArea / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfPersonInTheQueue() {
        // queue time = waiting time - service time
        double area = peopleArea - perPersonServiceTime;
        return area / ClockHandler.getInstance().getClock();
    }

    public double getAvgNumberOfGroupInTheQueue() {
        // queue time = waiting time - service time
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

    public long getNumberOfServedGroup(QueuePriority priority) {
        return this.groupPerPriority.get(priority);
    }

    public long getNumberOfServedPerson(QueuePriority priority) {
        return this.personPerPriority.get(priority);
    }

    public void updateAreas(long groups, long people) {
        double currentEventTime = ClockHandler.getInstance().getClock();

        if (groups < 0 || people < 0 || currentEventTime - previousEventTime < 0)
            throw new RuntimeException();

        groupsArea += (currentEventTime - previousEventTime) * groups;
        peopleArea += (currentEventTime - previousEventTime) * people;

        previousEventTime = currentEventTime;
    }

    public void endServiceUpdate(double serviceTime, int personServed, QueuePriority priority) {
        this.perGroupServiceTime += serviceTime;
        this.perPersonServiceTime += serviceTime * personServed;
        updateNumberOfJobServed(personServed, priority);
    }

    // Update the number of job and person served in this center
    private void updateNumberOfJobServed(int personServed, QueuePriority priority) {
        // Increase the total number of jobs served
        this.numberOfServedGroup++;
        this.numberOfServedPerson += personServed;

        // Increase the number of jobs served with this priority
        long numberOfGroup = this.groupPerPriority.get(priority);
        this.groupPerPriority.put(priority, numberOfGroup + 1);

        // Increase the number of person served with this priority
        long numberOfPerson = this.personPerPriority.get(priority);
        this.personPerPriority.put(priority, numberOfPerson + personServed);
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

    public void setAggregatedQueueStats(QueueStats aggregatedQueueStats) {
        this.aggregatedQueueStats = aggregatedQueueStats;
    }

    public QueueStats getAggregatedQueueStats() {
        return this.aggregatedQueueStats;
    }

    // ##########################################################################################################################################
    // NEW STATS

    public void updateServiceArea(Double time, Integer groupSize) {
        this.serviceAreaGroup.updateArea(time, 1);
        this.serviceAreaPeople.updateArea(time, groupSize);
    }

    public void updateQueueArea(Double time, QueuePriority prio, Integer groupSize) {
        if (this.queueAreaPerPrioGroup.get(prio) == null) {
            this.queueAreaPerPrioGroup.put(prio, new AreaStats());
            this.queueAreaPerPrioPeople.put(prio, new AreaStats());
        }
        this.queueAreaPerPrioGroup.get(prio).updateArea(time, 1);
        this.queueAreaPerPrioPeople.get(prio).updateArea(time, groupSize);

        this.queueAreaGroup.updateArea(time, 1);
        this.queueAreaPeople.updateArea(time, groupSize);
    }

    public Double getServiceAreaValue(StatsType statsType) {
        AreaStats serviceArea;
        AreaStats queueArea;
        switch (statsType) {
            case GROUP:
                serviceArea = this.serviceAreaGroup;
                queueArea = this.queueAreaGroup;
                break;

            case PERSON:
                serviceArea = this.serviceAreaPeople;
                queueArea = this.queueAreaPeople;
                break;

            default:
                return null;
        }

        return serviceArea.getArea() + queueArea.getArea();
    }

    public AreaStats getServiceAreaStats(StatsType statsType) {
        return switch (statsType) {
            case GROUP -> this.serviceAreaGroup;
            case PERSON -> this.serviceAreaPeople;
            default -> null;
        };
    }

    public AreaStats getQueueAreaStats(StatsType statsType, QueuePriority queuePrio) {

        if (queuePrio == null) {
            return switch (statsType) {
                case GROUP -> this.queueAreaGroup;
                case PERSON -> this.queueAreaPeople;
                default -> null;
            };
        }

        return switch (statsType) {
            case GROUP -> this.queueAreaPerPrioGroup.get(queuePrio);
            case PERSON -> this.queueAreaPerPrioPeople.get(queuePrio);
            default -> null;
        };

    }
}
