package it.uniroma2.pmcsn.parks.model.server;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction implements Center<RiderGroup> {

    private String name ;
    private double popularity;
    private double avgDuration;
    private QueueManager<RiderGroup> queueManager ;
    private int numberOfSeats ;
    private List<RiderGroup> currentServing = null ;
    private double currentServiceTime ;

    private int streamIndex ;

    public Attraction(String name, int numberOfSeats, double popularity, double avgDuration) {
        this.name = name ;
        this.popularity = popularity;
        this.avgDuration = avgDuration;
        this.numberOfSeats = numberOfSeats ;
        this.queueManager = new AttractionQueueManager() ;
        this.currentServing = null ;
        this.currentServiceTime = 0.0 ;
        
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
        this.currentServing = servingList ;

        double serviceTime = RandomHandler.getInstance().getUniform(streamIndex, 0, 1) ; 
        this.currentServiceTime = serviceTime ;

        return serviceTime ;
        // TODO Schedule endService event
        
    }

    @Override
    public List<RiderGroup> endService() {
        if (currentServing == null) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }
        for (RiderGroup riderGroup : currentServing) {
            riderGroup.getGroupStats().incrementRidesInfo(this.name, currentServiceTime);
        }
        List<RiderGroup> terminatedList = currentServing ;
        this.currentServing = null ;
        this.currentServiceTime = 0 ;

        return terminatedList ;

        // TODO Schedule startService event
    }


}
