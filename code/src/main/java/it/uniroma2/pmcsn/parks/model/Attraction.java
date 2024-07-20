package it.uniroma2.pmcsn.parks.model;
import it.uniroma2.pmcsn.parks.utils.RandomSingleton;

public class Attraction {

    private Queue queue ;
    private int numberOfSeats ;

    public int streamIndex ;

    public Attraction(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats ;
        this.queue = new AttractionQueue() ;

        this.streamIndex = RandomSingleton.getInstance().getNewStreamIndex() ;
    }

    public void arrival(RiderGroup group, double currentTime) {
        queue.enqueue(group, currentTime);
    }

    public void service(double currentTime) {
        int usedSeats = 0 ;
        while (true) {
            if (queue.getNextSize() <= numberOfSeats - usedSeats && queue.getNextSize() != 0) {
                RiderGroup riderGroup = queue.dequeue(currentTime) ;
                if (riderGroup == null) {
                    // No one in code to serve
                    break ;
                }
                usedSeats += riderGroup.getGroupSize() ;
            }
        }

        // double serviceTime = RandomSingleton.getInstance().getUniform(streamIndex, 0, 1) ;
        // double endTime = currentTime + serviceTime ;
    }

    public void endService() {

    }


}
