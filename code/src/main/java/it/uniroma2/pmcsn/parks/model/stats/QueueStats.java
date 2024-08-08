package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class QueueStats {

    private QueuePriority priority;
    private double perGroupQueueingTime;
    private double perPersonQueueingTime;
    private long numberOfGroup;
    private long numberOfPerson;

    public QueueStats(QueuePriority priority) {
        this.priority = priority;
        this.perGroupQueueingTime = 0;
        this.numberOfGroup = 0;
        this.numberOfPerson = 0;
    }

    public long getNumberOfGroupEnqueued() {
        return numberOfGroup;
    }

    public long getNumberOfPersonEnqueued() {
        return numberOfPerson;
    }

    public QueuePriority getPriority() {
        return priority;
    }

    public void updateStats(double queueingTime, int groupSize) {
        this.perGroupQueueingTime += queueingTime;
        this.perPersonQueueingTime += queueingTime * groupSize;
        this.numberOfGroup++;
        this.numberOfPerson += groupSize;
    }

    public double getAvgQueueingTimePerGroups() {
        if (numberOfGroup == 0) {
            return 0;
        }
        return perGroupQueueingTime / numberOfGroup;
    }

    public double getAvgQueueingTimePerPerson() {
        if (numberOfPerson == 0) {
            return 0;
        }
        return perPersonQueueingTime / numberOfPerson;
    }

}
