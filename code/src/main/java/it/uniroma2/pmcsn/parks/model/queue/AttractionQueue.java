package it.uniroma2.pmcsn.parks.model.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;



public class AttractionQueue implements Queue<RiderGroup> {

    private List<EnqueuedItem<RiderGroup>> queueList ;

    public AttractionQueue() {
        this.queueList = new ArrayList<>() ;
    }

    @Override
    public void enqueue(EnqueuedItem<RiderGroup> enqueuedGroup) {
        queueList.add(enqueuedGroup) ;
    }

    @Override
    public RiderGroup dequeue(double exitTime) {
        RiderGroup riderGroup = null ;
        
        if (queueList.size() > 0) {
            EnqueuedItem<RiderGroup> enqueuedGroup = queueList.remove(0) ;
            riderGroup = enqueuedGroup.getGroup() ;
            double entranceTime = enqueuedGroup.getQueueEntranceTime() ;
            riderGroup.getGroupStats().incrementQueueTime(exitTime - entranceTime) ;
        }
        return riderGroup ;
    }

    @Override
    public int getNextSize() {
        if (queueList.size() > 0) {
            EnqueuedItem<RiderGroup> enqueuedGroup = queueList.get(0) ;
            return enqueuedGroup.getGroup().getGroupSize() ;
            
        }
        return 0 ;
    }

}
