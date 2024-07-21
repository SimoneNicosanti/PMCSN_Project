package it.uniroma2.pmcsn.parks.model.job;

public class RiderGroup {

    private int groupSize ;
    private double queueTime ;
    private double ridingTime ;
    private double systemEntranceTime ;
    private int numberOfRides ;
    private GroupPriority priority ;

    //TODO Add number of rides per attraction --> Map<String,Int> / Map<Enum,Int>


    public RiderGroup(int groupSize, GroupPriority groupPriority) {
        this.groupSize = groupSize ;
        this.queueTime = 0.0 ;
        this.ridingTime = 0.0 ;
        this.systemEntranceTime = 0.0 ;
        this.numberOfRides = 0 ;
        this.priority = groupPriority ;
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

    public GroupPriority getPriority() {
        return priority;
    }

    
    public void incrementQueueTime(double queueTimeInc) {
        this.queueTime += queueTimeInc ;
    }

    public void incrementRidesInfo(double ridingTimeInc) {
        this.ridingTime += ridingTimeInc ;
        this.numberOfRides++ ;
    }

    public void changeAfterRide(double queueTimeInc, double ridingTimeInc) {
        this.queueTime += queueTimeInc ;
        this.ridingTime += ridingTimeInc ;
        this.numberOfRides++ ;
    
    }




}
