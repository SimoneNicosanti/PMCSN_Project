package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.CenterManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventBuilder {
    private CenterManager<RiderGroup> centerManager;

    public EventBuilder(CenterManager<RiderGroup> centerManager) {
        this.centerManager = centerManager;
    }

    public <T> Event<T> buildEventFrom(Event<T> event, EventType newEventType) {
        if (event.getEventType() == newEventType) {
            throw new RuntimeException("Cannot produce a " + newEventType + " event from itself");
        }
        Center<T> center = event.getEventCenter();
        EventsPoolId poolId = new EventsPoolId(center.getName(), newEventType);
        return new Event<>(poolId, center, ClockHandler.getInstance().getClock(), event.getJob());
    }

    // ** Builds an arrival associated to the Entrance Center */
    public Event<RiderGroup> buildEntranceNewArrivalEvent(double arrivalTime) {
        Center<RiderGroup> entranceCenter = centerManager.getCenterByName(Config.ENTRANCE);
        // Build the event and add the stream associated to it
        EventsPoolId poolId = new EventsPoolId(entranceCenter.getName(), EventType.ARRIVAL);
        double currentTime = ClockHandler.getInstance().getClock();
        RiderGroup job = buildRiderGroup(currentTime);
        Event<RiderGroup> event = new Event<>(poolId, entranceCenter, currentTime + arrivalTime, job);

        return event;
    }

    private RiderGroup buildRiderGroup(double currentTime) {
        int groupSize = 0; // TODO find a distribution for group sizes
        GroupPriority priority = GroupPriority.NORMAL; // TODO find a distribution for group priorities

        return new RiderGroup(groupSize, priority, currentTime);
    }

}
