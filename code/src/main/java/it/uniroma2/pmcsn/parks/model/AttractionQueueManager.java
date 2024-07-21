package it.uniroma2.pmcsn.parks.model;

import java.util.ArrayList;
import java.util.List;

public class AttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue priorityQueue ;
    private Queue normalQueue ;

    public AttractionQueueManager() {
        this.priorityQueue = new AttractionQueue() ;
        this.normalQueue = new AttractionQueue() ;
    }

    @Override
    public void addToQueues(RiderGroup group, double currentTime) {
        switch (group.getPriority()) {
            case PRIORITY:
                priorityQueue.enqueue(group, currentTime);
                break ;
            case NORMAL:
                normalQueue.enqueue(group, currentTime);
                break ;
        }
        
    }

    @Override
    public List<RiderGroup> extractFromQueues(int numberOfSeats, double currentTime) {
        List<RiderGroup> extractedList = new ArrayList<>() ;
        int usedSeats = 0 ;
        while (true) {
            if (priorityQueue.getNextSize() <= numberOfSeats - usedSeats && priorityQueue.getNextSize() != 0) {
                RiderGroup riderGroup = priorityQueue.dequeue(currentTime) ;
                if (riderGroup == null) {
                    // No one in code to serve --> Go to next queue
                    continue ;
                }
                usedSeats += riderGroup.getGroupSize() ;
                extractedList.add(riderGroup) ;
            } else if (normalQueue.getNextSize() <= numberOfSeats - usedSeats && normalQueue.getNextSize() != 0) {
                RiderGroup riderGroup = normalQueue.dequeue(currentTime) ;
                if (riderGroup == null) {
                    // No one in code to serve
                    break ;
                }
                usedSeats += riderGroup.getGroupSize() ;
                extractedList.add(riderGroup) ;
            }
        }
        return extractedList ;
    }

}
