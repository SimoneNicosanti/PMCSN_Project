package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

// Implement this for queue custom attributes
public class QueueStats {

    private QueuePriority priority;
    private double totalWaitingTime;
    private long numberOfItems;

    public QueueStats(QueuePriority priorityName) {
        this.priority = priorityName;
        this.totalWaitingTime = 0;
        this.numberOfItems = 0;
    }

    public QueuePriority getPriority() {
        return priority;
    }

    public void updateStats(double waitingTime, int numberOfPerson) {
        this.totalWaitingTime += waitingTime;
        this.numberOfItems += numberOfPerson;
    }

    public double getAvgWaitingTime() {
        return totalWaitingTime / numberOfItems;
    }

}
