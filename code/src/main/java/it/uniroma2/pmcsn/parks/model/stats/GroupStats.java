package it.uniroma2.pmcsn.parks.model.stats;

import java.util.HashMap;
import java.util.Map;

public class GroupStats {

    // The total service time of a center
    protected double serviceTime;
    // The total queue time of a center
    protected double queueTime;
    private double systemEntranceTime;
    // Number of rides per attraction
    private Map<String, Integer> numberOfRidesPerAttraction;

    public GroupStats(double systemEntranceTime) {
        this.serviceTime = 0;
        this.queueTime = 0;
        this.systemEntranceTime = systemEntranceTime;
        this.numberOfRidesPerAttraction = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getNumberOfRidesPerAttraction() {
        return this.numberOfRidesPerAttraction;
    }

    public double getSystemEntranceTime() {
        return this.systemEntranceTime;
    }

    public void incrementQueueTime(double queueTimeInc) {
        this.queueTime += queueTimeInc;
    }

    public void incrementRidesInfo(String centerName, double ridingTimeInc) {
        this.serviceTime += ridingTimeInc;
        Integer count = numberOfRidesPerAttraction.get(centerName);
        if (count == null)
            // Case in which the attraction is not in the map yet
            count = 0;
        this.numberOfRidesPerAttraction.put(centerName, count + 1);
    }

    public void changeAfterRide(String centerName, double queueTimeInc, double ridingTimeInc) {
        this.queueTime += queueTimeInc;
        this.incrementRidesInfo(centerName, ridingTimeInc);
    }

    public int getVisitsPerAttraction(String attractionName) {
        Integer numberOfRides = numberOfRidesPerAttraction.get(attractionName);
        if (numberOfRides == null) {
            return 0;
        }
        return numberOfRides;
    }

    public int getTotalNumberOfRides() {
        int sum = 0;
        for (int visits : numberOfRidesPerAttraction.values()) {
            sum += visits;
        }

        return sum;
    }

    public double getServiceTime() {
        return this.serviceTime;
    }

    public double getQueueTime() {
        return this.queueTime;
    }
}