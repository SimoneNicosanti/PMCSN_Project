package it.uniroma2.pmcsn.parks.model.server;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;

public class Restaurant implements Center<RiderGroup> {
    
    private String name ;
    private double popularity;
    private double avgDuration;
    private QueueManager<RiderGroup> queueManager ;
    private int numberOfSeats ;
    private List<ServingGroup> currentServing;

    private int streamIndex ;

    public Restaurant(String name, int numberOfSeats, double popularity, double avgDuration) {
        this.name = name ;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
        this.numberOfSeats = numberOfSeats ;
        this.queueManager = null; //TODO new RestaurantQueueManager() ;
        this.currentServing = new ArrayList<>() ;
        
        this.streamIndex = RandomHandler.getInstance().getNewStreamIndex() ;
    }

    public String getName() {
        return this.name;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

    public boolean isServing() {
        return currentServing != null && queueManager.areQueuesEmpty();
    }

    @Override
    public void arrival(RiderGroup group, double currentTime) {
        currentTime = ClockHandler.getInstance().getClock();
        queueManager.addToQueues(group, currentTime) ;

        // TODO Schedule startService event
    }

    @Override
    public double startService(double currentTime) {
        if (currentServing != null) {
            throw new RuntimeException("Cannot start a new service because there are still riders to serve");
        }
        List<RiderGroup> servingList =  queueManager.extractFromQueues(numberOfSeats, currentTime);
        
        // Save the current time for each group
        for (RiderGroup riderGroup : servingList) {
            this.currentServing.add(new ServingGroup(riderGroup, currentTime));
        }

        double serviceTime = RandomHandler.getInstance().getUniform(streamIndex, 0, 1) ;

        return serviceTime ;
        // TODO Schedule endService event
        
    }

    @Override
    public List<RiderGroup> endService(RiderGroup targetGroup) { //TODO we need to know the group that finished... this doesn't respect the interface
        if (currentServing.isEmpty()) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }

        ServingGroup groupToDelete = null;

        // Looking for the target group...
        for (ServingGroup group : currentServing) {
            if(group.getGroup().equals(targetGroup)) {
                // Group found! :)
                groupToDelete = group;
            }
        }

        if(groupToDelete == null) {
            // Group not found :(
            throw new RuntimeException("Cannot end service because the target group is not in the restaurant");
        }

        currentServing.remove(groupToDelete);

        return null ;

        // TODO Schedule startService event
    }
}
