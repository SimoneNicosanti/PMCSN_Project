package it.uniroma2.pmcsn.parks.model;
import java.util.List;

import it.uniroma2.pmcsn.parks.utils.RandomSingleton;

public class Attraction {

    private QueueManager queueManager ;
    private int numberOfSeats ;

    public int streamIndex ;

    public Attraction(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats ;
        this.queueManager = new AttractionQueueManager() ;
        
        this.streamIndex = RandomSingleton.getInstance().getNewStreamIndex() ;
    }

    public void arrival(RiderGroup group, double currentTime) {
        queueManager.addToQueues(group, currentTime) ;
        
    }

    public void service(double currentTime) {
        List<RiderGroup> servingList =  queueManager.extractFromQueues(numberOfSeats, currentTime);
        

        // double serviceTime = RandomSingleton.getInstance().getUniform(streamIndex, 0, 1) ;
        // double endTime = currentTime + serviceTime ;
    }

    public void endService() {

    }


}
