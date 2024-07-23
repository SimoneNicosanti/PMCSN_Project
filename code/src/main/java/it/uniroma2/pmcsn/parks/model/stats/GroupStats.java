package it.uniroma2.pmcsn.parks.model.stats;

import java.util.HashMap;
import java.util.Map;

public class GroupStats {

    private double queueTime;
    private double ridingTime;
    private double systemEntranceTime;
    private Map<String, Integer> numberOfRidesPerAttraction; // number of rides per attraction

    public GroupStats(double systemEntranceTime) {
        this.queueTime = 0;
        this.ridingTime = 0;
        this.systemEntranceTime = systemEntranceTime;
        this.numberOfRidesPerAttraction = new HashMap<String, Integer>();
    }

    public double getQueueTime() {
        return this.queueTime;
    }

    public double getRidingTime() {
        return this.ridingTime;
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
        this.ridingTime += ridingTimeInc;
        this.numberOfRidesPerAttraction.put(centerName, numberOfRidesPerAttraction.get(centerName) + 1);
    }

    public void changeAfterRide(String centerName, double queueTimeInc, double ridingTimeInc) {
        this.queueTime += queueTimeInc;
        this.ridingTime += ridingTimeInc;
        this.numberOfRidesPerAttraction.put(centerName, numberOfRidesPerAttraction.get(centerName) + 1);

    }

    public int getVisitsPerAttraction(String attractionName) {
        Integer numberOfRides = numberOfRidesPerAttraction.get(attractionName);
        if (numberOfRides == null) {
            return 0;
        }
        return numberOfRides;
    }

    public int getTotalNumberOfVisits() {
        int sum = 0;
        for (int visits : numberOfRidesPerAttraction.values()) {
            sum += visits;
        }

        return sum;
    }
}