package it.uniroma2.pmcsn.parks.model.server;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Attraction {

    private QueueManager<RiderGroup> queueManager ;
    private int numberOfSeats ;
    private List<RiderGroup> currentServing = null ;
    private double currentServiceTime ;

    private int streamIndex ;

    public Attraction(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats ;
        this.queueManager = new AttractionQueueManager() ;
        this.currentServing = null ;
        this.currentServiceTime = 0.0 ;
        
        this.streamIndex = RandomHandler.getInstance().getNewStreamIndex() ;
    }

    public void arrival(RiderGroup group, double currentTime) {
        currentTime = ClockHandler.getInstance().getClock();
        queueManager.addToQueues(group, currentTime) ;

        // TODO Schedule startService event
    }

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


    public List<RiderGroup> endService() {
        if (currentServing == null) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }
        for (RiderGroup riderGroup : currentServing) {
            riderGroup.incrementRidesInfo(currentServiceTime);
        }
        List<RiderGroup> terminatedList = currentServing ;
        this.currentServing = null ;
        this.currentServiceTime = 0 ;

        return terminatedList ;

        // TODO Schedule startService event
    }


}
