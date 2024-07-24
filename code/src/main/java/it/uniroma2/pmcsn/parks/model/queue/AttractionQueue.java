package it.uniroma2.pmcsn.parks.model.queue;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class AttractionQueue extends StatsQueue<RiderGroup> {

    public AttractionQueue(Queue<RiderGroup> queue) {
        super(queue);
    }

    @Override
    protected void retrieveStats(RiderGroup group, double entranceTime, double waitingTime) {

        group.getGroupStats().incrementQueueTime(waitingTime);
        queueStats.updateStats(waitingTime);
    }

}
