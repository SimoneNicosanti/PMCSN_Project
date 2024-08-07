package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

// Implement this for queue custom attributes
public class QueueStats {

    private QueuePriority priority;
    private double perGroupQueueingTime;
    private double perPersonQueueingTime;

    public QueueStats(QueuePriority priority) {
        this.priority = priority;
        this.perGroupQueueingTime = 0;
    }

    public QueuePriority getPriority() {
        return priority;
    }

    public void updateStats(double queueingTime, int groupSize) {
        this.perGroupQueueingTime += queueingTime;
        this.perPersonQueueingTime += queueingTime * groupSize;
    }

    public double getAvgQueueingTimePerGroups(long numberOfGroup) {
        if (numberOfGroup == 0)
            return 0;
        return perGroupQueueingTime / numberOfGroup;
    }

    public double getAvgQueueingTimePerPerson(long numberOfPerson) {
        if (numberOfPerson == 0)
            return 0;
        return perPersonQueueingTime / numberOfPerson;
    }

}
