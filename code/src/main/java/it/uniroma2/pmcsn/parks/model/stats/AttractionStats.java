package it.uniroma2.pmcsn.parks.model.stats;

public class AttractionStats {

    private double ridingTime;  // Total riding time of the attraction
    private long numberOfRides; // number of rides in the attraction

    public AttractionStats() {
        this.ridingTime = 0;
        this.numberOfRides = 0L;
    }

    public double getRidingTime() {
        return this.ridingTime ;
    }

    public long getNumberOfRides() {
        return this.numberOfRides;
    }

    public void addRideInformation(double ridingTimeInc, int numberOfRiders) {
        this.ridingTime += ridingTimeInc;
        this.numberOfRides += numberOfRiders;
    }
}