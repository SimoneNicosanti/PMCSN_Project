package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

// Implement this for queue custom attributes
public class QueueStats {

    private QueuePriority priority;
    private double perGroupWaitingTime;
    private double perPersonWaitingTime;
    private long numberOfGroup;
    private long numberOfPerson;

    public QueueStats(QueuePriority priority) {
        this.priority = priority;
        this.perGroupWaitingTime = 0;
        this.numberOfGroup = 0;
        this.numberOfPerson = 0;
    }

    public QueuePriority getPriority() {
        return priority;
    }

    public void updateStats(double waitingTime, int groupSize) {
        this.perGroupWaitingTime += waitingTime;
        this.perPersonWaitingTime += waitingTime * groupSize;
        this.numberOfGroup++;
        this.numberOfPerson += groupSize;
    }

    public long getNumberOfPerson() {
        return numberOfPerson;
    }

    public long getNumberOfGroup() {
        return numberOfGroup;
    }

    public double getAvgWaitingTimePerGroups() {
        if (numberOfGroup == 0)
            return 0;
        return perGroupWaitingTime / numberOfGroup;
    }

    public double getAvgWaitingTimePerPerson() {
        if (numberOfPerson == 0)
            return 0;
        return perPersonWaitingTime / numberOfPerson;
    }

}
