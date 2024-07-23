package it.uniroma2.pmcsn.parks.model.stats;

// Implement this for queue custom attributes
public class QueueStats {

    private double totalWaitingTime;
    private long numberOfItems;

    public QueueStats() {
        this.totalWaitingTime = 0;
        this.numberOfItems = 0;
    }

    public void updateStats(double waitingTime) {
        this.totalWaitingTime += waitingTime;
        this.numberOfItems++;
    }

    public double getAvgWaitingTime() {
        return totalWaitingTime / numberOfItems;
    }

}
