package it.uniroma2.pmcsn.parks.model.queue;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EntranceQueue extends StatsQueue<RiderGroup> {

    public EntranceQueue(Queue<RiderGroup> queue) {
        super(queue);
    }

    @Override
    protected void retrieveStats(RiderGroup group, double entranceTime, double waitingTime) {

        queueStats.updateStats(waitingTime);
    }

}
