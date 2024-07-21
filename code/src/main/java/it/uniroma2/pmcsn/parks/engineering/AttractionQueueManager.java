package it.uniroma2.pmcsn.parks.engineering;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.AttractionQueue;
import it.uniroma2.pmcsn.parks.model.queue.EnqueuedItem;

public class AttractionQueueManager implements QueueManager<RiderGroup> {

    private Queue<RiderGroup> priorityQueue ;
    private Queue<RiderGroup> normalQueue ;

    public AttractionQueueManager() {
        this.priorityQueue = new AttractionQueue() ;
        this.normalQueue = new AttractionQueue() ;
    }

    @Override
    public void addToQueues(RiderGroup group, double currentTime) {
        EnqueuedItem<RiderGroup> enqueuedGroup = new EnqueuedItem<RiderGroup>(group, currentTime);
        switch (group.getPriority()) {
            case PRIORITY:
                priorityQueue.enqueue(enqueuedGroup);
                break ;
            case NORMAL:
                normalQueue.enqueue(enqueuedGroup);
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
