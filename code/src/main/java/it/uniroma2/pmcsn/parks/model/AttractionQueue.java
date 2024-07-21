package it.uniroma2.pmcsn.parks.model;

import java.util.ArrayList;
import java.util.List;

public class AttractionQueue implements Queue {

    private List<EnqueuedGroup> queueList ;

    public AttractionQueue() {
        this.queueList = new ArrayList<>() ;
    }

    @Override
    public void enqueue(RiderGroup group, double entranceTime) {
        EnqueuedGroup enqueuedGroup = new EnqueuedGroup(group, entranceTime) ;
        queueList.add(enqueuedGroup) ;
    }

    @Override
    public RiderGroup dequeue(double exitTime) {
        RiderGroup riderGroup = null ;
        
        if (queueList.size() > 0) {
            EnqueuedGroup enqueuedGroup = queueList.remove(0) ;
            riderGroup = enqueuedGroup.getGroup() ;
            double entranceTime = enqueuedGroup.getQueueEntranceTime() ;
            riderGroup.incrementQueueTime(exitTime - entranceTime) ;
        }
        return riderGroup ;
    }

    @Override
    public int getNextSize() {
        if (queueList.size() > 0) {
            EnqueuedGroup enqueuedGroup = queueList.get(0) ;
            return enqueuedGroup.getGroup().getGroupSize() ;
            
        }
        return 0 ;
    } 

}
