package it.uniroma2.pmcsn.parks.model.stats;

public class GeneralStats {
    // The total service time of a center
    protected double serviceTime;
    // The total queue time of a center
    protected double queueTime;

    protected GeneralStats() {
        this.serviceTime = 0;
        this.queueTime = 0;
    }

    public double getServiceTime() {
        return this.serviceTime;
    }

    public double getQueueTime() {
        return this.queueTime;
    }

}
