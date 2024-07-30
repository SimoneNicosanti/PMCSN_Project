package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.queue.EntranceQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.MultiServer;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

//** Jobs at entrance are served once per service execution, so when jobs arrive and they get enqueued, they are served one by one, and the time of service is weighted to the number of riders per group */
public class Entrance extends MultiServer {

    public Entrance(String name, int slotNumber, double avgServiceTime) {
        super(name, new EntranceQueueManager(), slotNumber, avgServiceTime);
    }

    @Override
    public void arrival(RiderGroup job) {
        this.manageArrival(job);

        SystemEvent<RiderGroup> newArrivalEvent = EventBuilder.getNewArrivalEvent(this);
        if (newArrivalEvent != null) {
            EventsPool.<RiderGroup>getInstance().scheduleNewEvent(newArrivalEvent);
            EventLogger.logEvent("Generated", newArrivalEvent);
        }
    }

    /*
     * Modeling entrance service time as a k-Erlang
     * In this case k is the number of people in the group
     */
    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        return RandomHandler.getInstance().getErlang(this.name, job.getGroupSize(), this.avgServiceTime);
    }
}
