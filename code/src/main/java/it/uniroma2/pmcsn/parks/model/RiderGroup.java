package it.uniroma2.pmcsn.parks.model;

public class RiderGroup {

    private int groupSize ;
    private double queueTime ;
    private double ridingTime ;
    private double systemEntranceTime ;
    private int numberOfRides ;


    public RiderGroup(int groupSize) {
        this.groupSize = groupSize ;
        this.queueTime = 0.0 ;
        this.ridingTime = 0.0 ;
        this.systemEntranceTime = 0.0 ;
        this.numberOfRides = 0 ;
    }


    public int getGroupSize() {
        return this.groupSize ;
    }

    public double getQueueTime() {
        return this.queueTime ;
    }

    public double getRidingTime() {
        return this.ridingTime ;
    }

    public int getNumberOfRides() {
        return this.numberOfRides;
    }

    public double getSystemEntranceTime() {
        return this.systemEntranceTime;
    }

    public void incrementQueueTime(double queueTimeInc) {
        this.queueTime += queueTimeInc ;
    }

    public void incrementRides(double ridingTimeInc) {
        this.ridingTime += ridingTimeInc ;
        this.numberOfRides++ ;
    }

    public void changeAfterRide(double queueTimeInc, double ridingTimeInc) {
        this.queueTime += queueTimeInc ;
        this.ridingTime += ridingTimeInc ;
        this.numberOfRides++ ;
    
    }

}
