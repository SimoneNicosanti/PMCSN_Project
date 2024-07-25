package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends Center {

    public Entrance(String name, int slotNumber) {
        super(name, new EntranceQueueManager(), slotNumber);

    }

    public double getArrivalInterval() {
        // TODO: not sure about distribution
        return RandomHandler.getInstance().getExponential(name, 0.1);
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return slotNumber - currentServingJobs.size() >= 1;
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        int freeSlots = slotNumber - currentServingJobs.size();
        return queueManager.extractFromQueues(freeSlots);
    }

    @Override
    protected void terminateService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);

        this.startService();
    }

    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        return job.getGroupSize() * RandomHandler.getInstance().getExponential(this.name, 0.5);
    }

    @Override
    public void arrival(RiderGroup job) {
        this.commonArrivalManagement(job);

        Event<RiderGroup> newArrivalEvent = EventBuilder.getNewArrivalEvent(this);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(newArrivalEvent);
        EventLogger.logEvent("Generated", newArrivalEvent);

    }

    @Override
    public void endService(RiderGroup endedJob) {
        this.commonEndManagement(endedJob);
    }

}
